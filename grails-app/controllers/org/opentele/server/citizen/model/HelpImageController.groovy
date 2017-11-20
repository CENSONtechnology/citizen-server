package org.opentele.server.citizen.model

import grails.plugin.springsecurity.annotation.Secured
import org.opentele.server.model.HelpImage
import org.opentele.server.util.HelpImageUtil
import org.opentele.server.core.model.types.PermissionName

class HelpImageController {
    static allowedMethods = [downloadimage: "GET"]

    @Secured(PermissionName.PATIENT_QUESTIONNAIRE_READ_ALL)
    def downloadimage() {
        HelpImage helpImageInstance = HelpImage.get(params.id as Long)

        if (helpImageInstance == null) {
            render(status: 404, contentType: 'application/json') {
                return ['message': 'Image not found',
                        'errors': ['resource': 'helpImage', 'field': 'id', 'code': 'not_found']]
            }

        } else {

            def file = new File(HelpImageUtil.getAndEnsureUploadDir(), helpImageInstance.filename)
            String base64EncodedContent = new String(Base64.encoder.encode(file.bytes))
            byte[] prefixedData = "data:${formatPrefix(file)};base64,${base64EncodedContent}".bytes

            def outputStream = response.getOutputStream()
            outputStream.write(prefixedData)

            outputStream.flush()
            outputStream.close()
        }
    }

    private def formatPrefix(File imageFile) {
        def fileName = imageFile.name
        def extensionStart = fileName.lastIndexOf(".")
        def extension = fileName.substring(extensionStart + 1)

        "image/${extension}"
    }
}
