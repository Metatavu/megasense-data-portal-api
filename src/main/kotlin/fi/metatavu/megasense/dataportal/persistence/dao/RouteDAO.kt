package fi.metatavu.megasense.dataportal.persistence.dao


import fi.metatavu.megasense.dataportal.persistence.model.Route
import fi.metatavu.megasense.dataportal.persistence.model.Route_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for routes
 */
@ApplicationScoped
class RouteDAO: AbstractDAO<Route>() {

    /**
     * Creates a route
     *
     * @param id An UUID for identification
     * @param name A string representing name of the saved route
     * @param routePoints A string representing route points
     * @param locationFromName The name of the starting location
     * @param locationToName The name of the starting location
     * @param creatorId id of the user who created this route
     * @param lastModifierId if of the last user to modify the route
     * @return created route
     */
    fun create (id: UUID, name: String, routePoints: String, locationFromName: String, locationToName: String, creatorId: UUID, lastModifierId: UUID): Route {
        val route = Route()
        route.id = id
        route.name = name
        route.locationFromName = locationFromName
        route.locationToName = locationToName
        route.routePoints = routePoints
        route.creatorId = creatorId
        route.lastModifierId = lastModifierId
        return persist(route)
    }

    /**
     * Lists routes
     *
     * @param userId id of the user to whom the routes belong
     *
     * @return routes
     */
    fun list(userId: UUID): List<Route> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Route::class.java)
        val root = criteria.from(Route::class.java)

        criteria.select(root)
        val restrictions = mutableListOf<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(Route_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        criteria.orderBy(criteriaBuilder.desc(root.get(Route_.createdAt)))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}