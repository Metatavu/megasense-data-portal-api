package fi.metatavu.megasense.dataportal.route

import fi.metatavu.megasense.dataportal.persistence.dao.ExposureInstanceDAO
import fi.metatavu.megasense.dataportal.persistence.dao.RouteDAO
import fi.metatavu.megasense.dataportal.persistence.model.Route
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller class for routes
 */
@ApplicationScoped
class RouteController {
    @Inject
    private lateinit var routeDAO: RouteDAO

    @Inject
    private lateinit var exposureInstanceDAO: ExposureInstanceDAO

    /**
     * Creates a route
     *
     * @param routePoints A string representing route points
     * @param locationFromName The name of the starting location
     * @param locationToName The name of the starting location
     * @param creatorId id of the user who created this route
     *
     * @return created route
     */
    fun createRoute (routePoints: String, locationFromName: String, locationToName: String, creatorId: UUID): Route {
        return routeDAO.create(UUID.randomUUID(), routePoints, locationFromName, locationToName, creatorId)
    }

    /**
     * Lists all routes
     *
     * @param userId id of the user to whom the routes belong
     *
     * @return routes
     */
    fun listRoutes (userId: UUID): List<Route> {
        return routeDAO.list(userId)
    }

    /**
     * Finds a route
     *
     * @param routeId id of the route to find
     *
     * @return found route or null if not found
     */
    fun findRoute (routeId: UUID): Route? {
        return routeDAO.findById(routeId)
    }

    /**
     * Deletes a route
     *
     * @param route a route to delete
     * @param userId id of the user who is deleting this route
     */
    fun deleteRoute (route: Route, userId: UUID) {
        val exposureInstancesToClear = exposureInstanceDAO.list(userId, null, null, route)
        for (exposureInstance in exposureInstancesToClear) {
            exposureInstanceDAO.clearRouteField(exposureInstance)
        }

        routeDAO.delete(route)
    }
}