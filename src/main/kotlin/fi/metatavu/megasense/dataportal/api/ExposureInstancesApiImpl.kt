package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.ExposureInstancesApi
import fi.metatavu.megasense.dataportal.api.spec.model.ExposureInstance
import fi.metatavu.megasense.dataportal.api.translate.ExposureInstanceTranslator
import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import fi.metatavu.megasense.dataportal.persistence.model.Route
import fi.metatavu.megasense.dataportal.route.RouteController
import java.time.OffsetDateTime
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * REST endpoints for exposure instances
 */
@Stateful
@RequestScoped
class ExposureInstancesApiImpl: ExposureInstancesApi, AbstractApi() {
    @Inject
    private lateinit var exposureInstanceTranslator: ExposureInstanceTranslator

    @Inject
    private lateinit var exposureInstanceController: ExposureInstanceController

    @Inject
    private lateinit var routeController: RouteController

    override fun createExposureInstance(exposureInstance: ExposureInstance): Response {
        var route: Route? = null
        if (exposureInstance.routeId != null) {
            route = routeController.findRoute(exposureInstance.routeId)
        }

        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.createExposureInstance(
                route,
                exposureInstance.startedAt,
                exposureInstance.endedAt,
                exposureInstance.carbonMonoxide.toFloat(),
                exposureInstance.nitrogenMonoxide.toFloat(),
                exposureInstance.nitrogenDioxide.toFloat(),
                exposureInstance.ozone.toFloat(),
                exposureInstance.sulfurDioxide.toFloat(),
                exposureInstance.harmfulMicroparticles.toFloat(),
                loggerUserId!!
        )))
    }

    override fun findExposureInstance(exposureInstanceId: UUID): Response {
        val exposureInstance = exposureInstanceController.findExposureInstance(exposureInstanceId) ?: return createNotFound("Exposure instance not found")
        return createOk(exposureInstanceTranslator.translate(exposureInstance))
    }

    override fun listExposureInstances(createdBefore: String?, createdAfter: String?): Response {
        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.listRoutes(OffsetDateTime.parse(createdAfter), OffsetDateTime.parse(createdBefore))))
    }

}