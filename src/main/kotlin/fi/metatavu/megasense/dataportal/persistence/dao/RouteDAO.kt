package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.persistence.model.Route
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for routes
 */
@ApplicationScoped
class RouteDAO: AbstractDAO<Route>() {

    /**
     * Creates a route
     *
     * @param id An UUID for identification
     * @param routePoints A string representing route points
     * @param creatorId id of the user who created this route
     *
     * @return created route
     */
    fun create (id: UUID, routePoints: String, creatorId: UUID): Route {
        val route = Route()
        route.id = id
        route.routePoints = routePoints
        route.creatorId = creatorId
        route.lastModifierId = creatorId
        return persist(route)
    }
}