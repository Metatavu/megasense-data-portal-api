package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.persistence.model.Route
import javax.enterprise.context.ApplicationScoped

/**
 * A translator class for routes
 */
@ApplicationScoped
class RouteTranslator: AbstractTranslator<fi.metatavu.megasense.dataportal.persistence.model.Route, fi.metatavu.megasense.dataportal.api.spec.model.Route>() {

    /**
     * Translates JPA routes into REST routes
     *
     * @param entity JPA route
     *
     * @return REST route
     */
    override fun translate(entity: Route): fi.metatavu.megasense.dataportal.api.spec.model.Route {
        val route = fi.metatavu.megasense.dataportal.api.spec.model.Route()
        route.id = entity.id
        route.name = entity.name
        route.routePoints = entity.routePoints
        route.locationFromName = entity.locationFromName
        route.locationToName = entity.locationToName
        route.savedAt = entity.createdAt
        return route
    }
}