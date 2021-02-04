package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.RoutesApi
import fi.metatavu.megasense.dataportal.api.spec.model.Route
import fi.metatavu.megasense.dataportal.api.translate.RouteTranslator
import fi.metatavu.megasense.dataportal.route.RouteController
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Endpoints for routes API
 */
@Stateful
@RequestScoped
class RoutesApiImpl: RoutesApi, AbstractApi() {

    @Inject
    private lateinit var routeController: RouteController

    @Inject
    private lateinit var routeTranslator: RouteTranslator

    override fun createRoute(route: Route): Response {
        return createOk(routeTranslator.translate(routeController.createRoute(route.name, route.routePoints, route.locationFromName, route.locationToName, loggerUserId!!)))
    }

    override fun deleteRoute(routeId: UUID): Response {
        val route = routeController.findRoute(routeId) ?: return createBadRequest("Route not found!")
        if (route.creatorId!! != loggerUserId!!) {
            return createNotFound("Route not found!")
        }
        routeController.deleteRoute(route, loggerUserId!!)
        return createNoContent()
    }

    override fun findRoute(routeId: UUID): Response {
        val route = routeController.findRoute(routeId) ?: return createNotFound("Route not found!")
        if (route.creatorId!! != loggerUserId!!) {
            return createNotFound("Route not found")
        }

        return createOk(routeTranslator.translate(route))
    }

    override fun listRoutes(): Response {
        return createOk(routeTranslator.translate(routeController.listRoutes(loggerUserId!!)))
    }
}