package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.RoutesApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.Route
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import java.util.*

class RoutesTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<Route, ApiClient> (testBuilder, apiClient) {
    /**
     * Sends a request to create a route
     *
     * @param name string representing a route name
     * @param routePoints string representing a route
     * @param locationFromName location from name
     * @param locationToName location to name
     *
     * @return created route
     */
    fun create (name:String, routePoints: String, locationFromName: String, locationToName: String): Route {
        val route = Route(name, routePoints, locationFromName, locationToName)
        return addClosable(api.createRoute(route))
    }

    /**
     * Sends a request to list all routes
     *
     * @return all routes created by the user
     */
    fun listAll (): Array<Route> {
        return api.listRoutes()
    }

    /**
     * Sends a request to find a route
     *
     * @param routeId id of the route to find
     *
     * @return found route
     */
    fun find (routeId: UUID): Route {
        return api.findRoute(routeId)
    }

    override fun clean (route: Route) {
        api.deleteRoute(route.id!!)
    }

    override fun getApi (): RoutesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return RoutesApi(TestSettings.apiBasePath)
    }
}