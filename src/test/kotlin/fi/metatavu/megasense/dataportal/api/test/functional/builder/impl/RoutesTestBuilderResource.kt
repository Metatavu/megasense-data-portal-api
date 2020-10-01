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
     * @param routePoints string representing a route
     *
     * @return created route
     */
    fun create (routePoints: String): Route {
        val route = Route(routePoints)
        return api.createRoute(route)
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

    override fun clean (route: Route?) {
        TODO("Not yet implemented")
    }

    override fun getApi (): RoutesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return RoutesApi(TestSettings.apiBasePath)
    }
}