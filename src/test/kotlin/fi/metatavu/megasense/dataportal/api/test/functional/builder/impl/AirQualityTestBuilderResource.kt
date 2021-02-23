package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.AirQualityApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.AirQuality
import fi.metatavu.megasense.dataportal.api.client.models.RouteAirQuality
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings

/**
 * Test builder resource for handling air quality
 */
class AirQualityTestBuilderResource (testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<AirQuality, ApiClient> (testBuilder, apiClient) {
    override fun clean(t: AirQuality?) {
        //air quality is read from file -> nothing to clean up
    }

    /**
     * Returns air quality values
     *
     * @param pollutantType return only values for this pollutant
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     * @return air quality values
     */
    fun getAirQuality (pollutantType: String, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        return api.getAirQuality(pollutantType, boundingBoxCorner1, boundingBoxCorner2).toList()
    }

    /**
     * Returns air quality for specific coordinates
     *
     * @param coordinates coordinates
     * @param pollutantType type of the pollutant
     * @return air quality
     */
    fun getAirQualityForCoordinates (coordinates: String, pollutantType: String): AirQuality {
        return api.getAirQualityForCoordinates(coordinates, pollutantType)
    }

    /**
     * Returns air quality data for list of coordinates
     *
     * @param coordinates coordinates list
     * @return air quality for coordinates list
     */
    fun getAirQualityForRouteCoordinates (coordinates: List<String>): List<RouteAirQuality> {
        return api.getRouteAirQuality(coordinates).toList()
    }

    override fun getApi(): AirQualityApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return AirQualityApi(TestSettings.apiBasePath)
    }
}