package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.TotalExposureApi
import fi.metatavu.megasense.dataportal.api.translate.ExposureInstanceTranslator
import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import java.time.OffsetDateTime
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * The REST endpoint for total exposure
 */
@Stateful
@RequestScoped
class TotalExposureApiImpl: TotalExposureApi, AbstractApi() {
    @Inject
    private lateinit var exposureInstanceTranslator: ExposureInstanceTranslator

    @Inject
    private lateinit var exposureInstanceController: ExposureInstanceController

    override fun totalExposure(exposedBefore: String?, exposedAfter: String?): Response {
        var exposedBeforeDate: OffsetDateTime? = null
        var exposedAfterDate: OffsetDateTime? = null

        if (exposedBefore != null) {
            exposedBeforeDate = OffsetDateTime.parse(exposedBefore)
        }

        if (exposedAfter != null) {
            exposedAfterDate = OffsetDateTime.parse(exposedAfter)
        }

        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.getTotalExposure(loggerUserId!!, exposedBeforeDate, exposedAfterDate)))
    }

}