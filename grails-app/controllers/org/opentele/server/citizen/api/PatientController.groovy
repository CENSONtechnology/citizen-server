package org.opentele.server.citizen.api

import grails.plugin.springsecurity.annotation.Secured
import org.opentele.server.core.model.types.PermissionName
import org.opentele.server.model.LinksCategory
import org.opentele.server.model.Patient

@Secured(PermissionName.NONE)
class PatientController {

    static allowedMethods = [show: "GET"]

    def grailsApplication
    def springSecurityService
    def citizenMessageService

    @Secured(PermissionName.PATIENT_LOGIN)
    def show() {
        def user = springSecurityService.currentUser
        def patient = Patient.findByUser(user)

        def body = [
                'firstName': patient.firstName,
                'lastName': patient.lastName,
                'uniqueId': patient.cpr,
                'passwordExpired': patient.user.cleartextPassword ? true : false,
                'links': [
                        'self': createLink(mapping: 'patient', absolute: true),
                        'password': createLink(mapping: 'password', absolute: true),
                        'measurements': createLink(mapping: 'measurements', absolute: true),
                        'questionnaires': createLink(mapping: 'questionnaires', absolute: true),
                        'reminders':  createLink(mapping: 'reminders', absolute: true)
                ]
        ]

        if (citizenMessageService.isMessagesAvailableTo(patient)) {
            body.links['messageThreads'] = createLink(mapping: 'messageThreads', absolute: true)
            body.links['unreadMessages'] = createLink(mapping: 'messages', absolute: true)
            body.links['acknowledgements'] = createLink(mapping: 'acknowledgements', absolute: true)
        }

        if (Boolean.valueOf(grailsApplication.config.video.enabled)) {
            body.links['videoPendingConference'] = createLink(mapping: 'videoPendingConference', absolute: true)
            body.links['patientHasPendingMeasurement'] = createLink(mapping: 'patientHasPendingMeasurement', absolute: true)
            body.links['measurementFromPatient'] = createLink(mapping: 'measurementFromPatient', absolute: true)
        }

        Boolean continuousCtgEnabled = Boolean.valueOf(grailsApplication.config.continuousCtg.enabled)
        Boolean patientHasContinuousCTG = patient.patient2PatientGroups*.patientGroup.any { it.showRunningCtgMessaging }

        if (continuousCtgEnabled && patientHasContinuousCTG) {
            body.links['continuousCtg'] = grailsApplication.config.continuousCtg.url
        }

        if (hasAnyLinksCategories(patient)) {
            body.links['linksCategories'] = createLink(mapping: 'linksCategories', absolute: true)
        }

        [resource: body, resourceType: 'patient']
    }

    private static hasAnyLinksCategories(Patient patient) {
        def linksCategories = LinksCategory.byPatientGroups(patient.groups)
        linksCategories.size() > 0
    }
}
