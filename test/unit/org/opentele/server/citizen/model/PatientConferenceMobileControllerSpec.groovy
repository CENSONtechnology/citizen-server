package org.opentele.server.citizen.model


import grails.buildtestdata.mixin.Build
import grails.test.mixin.*
import org.opentele.server.model.Clinician
import org.opentele.server.model.Conference
import org.opentele.server.model.ConferenceBloodPressureMeasurementDraft
import org.opentele.server.model.ConferenceLungFunctionMeasurementDraft
import org.opentele.server.model.ConferenceSaturationMeasurementDraft
import org.opentele.server.model.DeviceOrigin
import org.opentele.server.model.Patient
import org.opentele.server.model.PendingConference
import org.opentele.server.video.VideoConferenceService
import spock.lang.Specification

@TestFor(PatientConferenceMobileController)
@Build([Patient, Clinician, Conference, PendingConference, ConferenceLungFunctionMeasurementDraft, ConferenceBloodPressureMeasurementDraft, ConferenceSaturationMeasurementDraft])
@Mock([Patient, Clinician, Conference, PendingConference, ConferenceLungFunctionMeasurementDraft, ConferenceBloodPressureMeasurementDraft, ConferenceSaturationMeasurementDraft])
class PatientConferenceMobileControllerSpec extends Specification {

    def videoConferenceService = Mock(VideoConferenceService)

    Patient patient
    Clinician clinician

    def setup() {
        controller.videoConferenceService = videoConferenceService
        clinician = Clinician.build()
    }

    def "receives 404 if no pending conference exists"() {
        setup:
        setPatientAsUser()

        when:
        controller.patientHasPendingConference()

        then:
        response.status == 404
    }

    def "receives pending conference when one exists"() {
        setup:
        setPatientAsUser()
        videoConferenceService.metaClass.userIsAlreadyPresentInOwnRoom = { user, password -> true }
        PendingConference.build(clinician: clinician, patient: patient, roomKey: "a_room_key_42")

        when:
        controller.patientHasPendingConference()

        then:
        response.text == '{"roomKey":"a_room_key_42","serviceUrl":"null"}'
    }

    def 'knows that patient has no pending measurements when no unfinished conferences exist'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient, completed: true)
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: true, waiting:true))

        when:
        controller.patientHasPendingMeasurement()

        then:
        response.text == '{}'
    }

    def 'knows that patient has no pending measurements when no automatic, waiting measurement drafts exist'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: true, waiting:false))
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: false))

        when:
        controller.patientHasPendingMeasurement()

        then:
        response.text == '{}'
    }

    def 'knows when patient has pending measurement'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: true, waiting:true))
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: false))

        when:
        controller.patientHasPendingMeasurement()

        then:
        response.text == '{"type":"LUNG_FUNCTION"}'
    }

    def 'can receive lung function measurement'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        def pendingLungFunctionMeasurement = ConferenceLungFunctionMeasurementDraft.build(automatic: true, waiting:true)
        conference.addToMeasurementDrafts(pendingLungFunctionMeasurement)
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: false))

        when:
        request.JSON = [
            type: 'LUNG_FUNCTION',
            origin: [device_measurement: [
                primary_device_identifier: [
                    serial_number: "501577857"
                ]
            ]],
            measurement: [
                fev1: 3.6,
                fev6: 5.7,
                fev1Fev6Ratio: 0.632,
                fef2575: 2.4,
                goodTest: true,
                softwareVersion: 935
            ]
        ]
        controller.measurementFromPatient()

        then:
        response.status == 200
        pendingLungFunctionMeasurement.automatic
        !pendingLungFunctionMeasurement.waiting
        pendingLungFunctionMeasurement.origin instanceof DeviceOrigin
        pendingLungFunctionMeasurement.origin.primaryIdentifier.type == 'serial_number'
        pendingLungFunctionMeasurement.origin.primaryIdentifier.value == '501577857'
        pendingLungFunctionMeasurement.fev1 == 3.6
        pendingLungFunctionMeasurement.fev6 == 5.7
        pendingLungFunctionMeasurement.fev1Fev6Ratio == 0.632
        pendingLungFunctionMeasurement.fef2575 == 2.4
        pendingLungFunctionMeasurement.goodTest
        pendingLungFunctionMeasurement.softwareVersion == 935
    }

    def 'can receive blood pressure measurement'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        def pendingBloodPressureMeasurement = ConferenceBloodPressureMeasurementDraft.build(automatic: true, waiting: true)
        conference.addToMeasurementDrafts(pendingBloodPressureMeasurement)
        conference.addToMeasurementDrafts(ConferenceBloodPressureMeasurementDraft.build(automatic: false))

        when:
        request.JSON = [
            type: 'BLOOD_PRESSURE',
            origin: [device_measurement: [
                primary_device_identifier: [
                    serial_number: "501577857"
                ]
            ]],
            measurement: [
                systolic: 123,
                diastolic: 57,
                pulse: 45,
                meanArterialPressure: 100
            ]
        ]
        controller.measurementFromPatient()

        then:
        response.status == 200
        pendingBloodPressureMeasurement.automatic
        !pendingBloodPressureMeasurement.waiting
        pendingBloodPressureMeasurement.origin instanceof DeviceOrigin
        pendingBloodPressureMeasurement.origin.primaryIdentifier.type == 'serial_number'
        pendingBloodPressureMeasurement.origin.primaryIdentifier.value == '501577857'
        pendingBloodPressureMeasurement.systolic == 123
        pendingBloodPressureMeasurement.diastolic == 57
        pendingBloodPressureMeasurement.pulse == 45
        pendingBloodPressureMeasurement.meanArterialPressure == 100
    }

    def 'can receive saturation measurement'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        def pendingSaturationMeasurement = ConferenceSaturationMeasurementDraft.build(automatic: true, waiting: true)
        conference.addToMeasurementDrafts(pendingSaturationMeasurement)
        conference.addToMeasurementDrafts(ConferenceSaturationMeasurementDraft.build(automatic: false))

        when:
        request.JSON = [
            type: 'SATURATION',
            origin: [device_measurement: [
                primary_device_identifier: [
                    serial_number: "501577857"
                ]
            ]],
            measurement: [
                saturation: 98,
                pulse: 57,
            ]
        ]
        controller.measurementFromPatient()

        then:
        response.status == 200
        pendingSaturationMeasurement.automatic
        !pendingSaturationMeasurement.waiting
        pendingSaturationMeasurement.origin instanceof DeviceOrigin
        pendingSaturationMeasurement.saturation == 98
        pendingSaturationMeasurement.pulse == 57
    }

    def 'does not complain when receiving measurement for which no pending measurements exist'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)

        when:
        request.JSON = [
            type:'LUNG_FUNCTION',
            measurement: [
                fev1: 3.6
            ]
        ]
        controller.measurementFromPatient()
        conference.refresh()

        then:
        conference.measurementDrafts.empty
    }

    def 'complains when receiving measurement of unknown type'() {
        setup:
        setPatientAsUser()
        def conference = Conference.build(clinician: clinician, patient: patient)
        conference.addToMeasurementDrafts(ConferenceLungFunctionMeasurementDraft.build(automatic: true, waiting:true))

        when:
        request.JSON = [
            type:'Unknown',
            measurement: [
                fev1: 3.6
            ]
        ]
        controller.measurementFromPatient()

        then:
        thrown(IllegalArgumentException)
    }

    private void setPatientAsUser() {
        patient = Patient.build()
        controller.metaClass.currentPatient = { -> patient }
    }

}
