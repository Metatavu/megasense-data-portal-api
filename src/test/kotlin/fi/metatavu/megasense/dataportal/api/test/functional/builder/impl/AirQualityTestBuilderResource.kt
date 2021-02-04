package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.AirQualityApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.AirQuality
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import org.junit.Assert.assertTrue
import java.lang.Exception

class AirQualityTestBuilderResource (testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<AirQuality, ApiClient> (testBuilder, apiClient) {
    override fun clean(t: AirQuality?) {
        TODO("Not yet implemented")
    }

    /**
     * Returns air quality values
     *
     * @param pollutantType return only values for this pollutant
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     *
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
     *
     * @return air quality
     */
    fun getAirQualityForCoordinates (coordinates: String, pollutantType: String): AirQuality {
        return api.getAirQualityForCoordinates(coordinates, pollutantType)
    }

    override fun getApi(): AirQualityApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return AirQualityApi(TestSettings.apiBasePath)
    }
}