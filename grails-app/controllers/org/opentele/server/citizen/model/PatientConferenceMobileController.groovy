package org.opentele.server.citizen.model

import dk.silverbullet.kih.api.auditlog.SkipAuditLog
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.opentele.server.core.model.ConferenceMeasurementDraftType
import org.opentele.server.core.model.types.PermissionName
import org.opentele.server.model.*
import org.springframework.beans.factory.annotation.Value

class PatientConferenceMobileController {

    @Value('${video.client.serviceURL:null}')
    String serviceUrl

    def springSecurityService
    def videoConferenceService

    // Patient
    @Secured(PermissionName.JOIN_VIDEO_CALL)
    @SkipAuditLog
    def patientHasPendingConference() {
        def patient = currentPatient()

        def pendingConferences = PendingConference.findAllByPatient patient
        if (!pendingConferences) {
            return render(status: 404)
        }

        def pendingConference = pendingConferences.find { pendingConference ->
            Clinician clinician = pendingConference.clinician
            clinician && clinicianHasJoinedRoom(clinician)
        }

        if (pendingConference) {
            render([roomKey: pendingConference.roomKey, serviceUrl: serviceUrl] as JSON)
        } else {
            render(status: 404)
        }
    }

    private boolean clinicianHasJoinedRoom(Clinician clinician) {
        videoConferenceService.userIsAlreadyPresentInOwnRoom(clinician.videoUser, clinician.videoPassword)
    }

    // Patient
    @Secured(PermissionName.JOIN_VIDEO_CALL)
    @SkipAuditLog
    def patientHasPendingMeasurement() {
        def patient = currentPatient()

        def waitingConferenceMeasurementDraft = waitingConferenceMeasurementDraft(patient,
                ConferenceMeasurementDraftType.BLOOD_PRESSURE,
                ConferenceMeasurementDraftType.LUNG_FUNCTION,
                ConferenceMeasurementDraftType.SATURATION)

        def reply = (waitingConferenceMeasurementDraft != null) ?
                [type: waitingConferenceMeasurementDraft.type.name()] :
                [:]

        render reply as JSON
    }


    // Patient
    @Secured(PermissionName.JOIN_VIDEO_CALL)
    def measurementFromPatient() {
        def patient = currentPatient()

        def measurementDetails = request.JSON
        ConferenceMeasurementDraftType measurementType = ConferenceMeasurementDraftType.find { it.name() == measurementDetails.type }
        if (measurementType == null) {
            throw new IllegalArgumentException("Unknown measurement type: '${measurementDetails.type}'")
        }

        ConferenceMeasurementDraft waitingConferenceMeasurementDraft = waitingConferenceMeasurementDraft(patient, measurementType)
        if (waitingConferenceMeasurementDraft == null) {
            // Don't update anything, and don't throw any exceptions
            render [:] as JSON
            return
        }

        switch (measurementType) {
            case ConferenceMeasurementDraftType.LUNG_FUNCTION:
                fillOutLungFunctionMeasurement(waitingConferenceMeasurementDraft, measurementDetails.measurement)
                break;
            case ConferenceMeasurementDraftType.BLOOD_PRESSURE:
                fillOutBloodPressureMeasurement(waitingConferenceMeasurementDraft, measurementDetails.measurement)
                break;
            case ConferenceMeasurementDraftType.SATURATION:
                fillOutSaturationMeasurement(waitingConferenceMeasurementDraft, measurementDetails.measurement)
                break;
            default:
                throw new IllegalArgumentException("Unsupported automatic measurement type: '${measurementType}'")
        }

        waitingConferenceMeasurementDraft.origin = Origin.fromJson(measurementDetails.origin)
        waitingConferenceMeasurementDraft.waiting = false
        render [:] as JSON
    }

    private static ConferenceMeasurementDraft waitingConferenceMeasurementDraft(Patient patient, ConferenceMeasurementDraftType... types) {
        def allUnfinishedConferences = Conference.findAllByPatientAndCompleted(patient, false, [sort: 'id'])
        def conferenceWithWaitingMeasurementDraft = allUnfinishedConferences.find {
            it.measurementDrafts.any { it.automatic && it.waiting && it.type in types }
        }
        if (conferenceWithWaitingMeasurementDraft == null) {
            return null
        }
        def sortedDrafts = conferenceWithWaitingMeasurementDraft.measurementDrafts.sort { it.id }
        sortedDrafts.find { it.automatic && it.waiting && it.type in types }
    }

    private static fillOutLungFunctionMeasurement(ConferenceLungFunctionMeasurementDraft draft, submittedMeasurement) {
        draft.fev1 = submittedMeasurement.fev1
        draft.fev6 = submittedMeasurement.fev6
        draft.fev1Fev6Ratio = submittedMeasurement.fev1Fev6Ratio
        draft.fef2575 = submittedMeasurement.fef2575
        draft.goodTest = submittedMeasurement.goodTest
        draft.softwareVersion = submittedMeasurement.softwareVersion
    }

    private static fillOutBloodPressureMeasurement(ConferenceBloodPressureMeasurementDraft draft, submittedMeasurement) {
        draft.systolic = submittedMeasurement.systolic
        draft.diastolic = submittedMeasurement.diastolic
        draft.pulse = submittedMeasurement.pulse
        draft.meanArterialPressure = submittedMeasurement.meanArterialPressure
    }

    private static fillOutSaturationMeasurement(ConferenceSaturationMeasurementDraft draft, submittedMeasurement) {
        draft.saturation = submittedMeasurement.saturation
        draft.pulse = submittedMeasurement.pulse
    }

    private currentPatient() {
        Patient.findByUser(springSecurityService.currentUser)
    }

}
