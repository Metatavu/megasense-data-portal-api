package fi.metatavu.megasense.dataportal.api.test.functional.builder.auth

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.test.functional.builder.impl.*
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings

/**
 * Test builder authentication
 *
 * Constructor
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider) : AuthorizedTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider
    private var routes: RoutesTestBuilderResource? = null
    private var favourites: FavouritesTestBuilderResource? = null
    private var exposureInstances: ExposureInstancesTestBuilderResource? = null
    private var totalExposure: TotalExposureTestBuilderResource? = null
    private var airQuality: AirQualityTestBuilderResource? = null
    private var users: UsersTestBuilderResource? = null

    /**
     * Creates a API client
     *
     * @param accessToken access token
     * @return API client
     */
    override fun createClient(accessToken: String): ApiClient {
        val result = ApiClient(TestSettings.apiBasePath)
        ApiClient.accessToken = accessToken
        return result
    }

    /**
     * Returns a test builder resource for routes
     *
     * @return a test builder resource for routes
     */
    fun routes (): RoutesTestBuilderResource {
        if (routes == null) {
            routes = RoutesTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return routes!!
    }

    /**
     * Returns a test builder resource for favourites
     *
     * @return a test builder resource for favourites
     */
    fun favourites (): FavouritesTestBuilderResource {
        if (favourites == null) {
            favourites = FavouritesTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return favourites!!
    }

    /**
     * Returns a test builder resource for exposure instances
     *
     * @return a test builder resource for exposure instances
     */
    fun exposureInstances (): ExposureInstancesTestBuilderResource {
        if (exposureInstances == null) {
            exposureInstances = ExposureInstancesTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return exposureInstances!!
    }

    /**
     * Returns a test builder resource for total exposure
     *
     * @return a test builder resource for total exposure
     */
    fun totalExposure (): TotalExposureTestBuilderResource {
        if (totalExposure == null) {
            totalExposure = TotalExposureTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return totalExposure!!
    }

    /**
     * Returns a test builder resource for air quality
     *
     * @return a test builder resource for air quality
     */
    fun airQuality (): AirQualityTestBuilderResource {
        if (airQuality == null) {
            airQuality = AirQualityTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return airQuality!!
    }

    /**
     * Returns a test builder resource for users API
     *
     * @return a test builder resource for users API
     */
    fun users (): UsersTestBuilderResource {
        if (users == null) {
            users = UsersTestBuilderResource(testBuilder, accessTokenProvider, createClient())
        }

        return users!!
    }
}