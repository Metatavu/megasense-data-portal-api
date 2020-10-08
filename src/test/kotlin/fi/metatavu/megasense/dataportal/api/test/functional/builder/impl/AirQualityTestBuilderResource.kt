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
     * @param precision precision in meters for the returned data
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     *
     * @return air quality values
     */
    fun getAirQuality (pollutantType: String, precision: Int, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        return api.getAirQuality(pollutantType, precision, boundingBoxCorner1, boundingBoxCorner2).toList()
    }

    /**
     * Asserts that air quality data has correct structure
     *
     * @param airQuality data to test
     */
    fun assertCorrectStructure (airQuality: List<AirQuality>) {
        assertTrue(airQuality.first().location.latitude < airQuality.last().location.latitude)
        assertTrue(airQuality.first().location.longitude < airQuality.last().location.longitude)
    }

    override fun getApi(): AirQualityApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return AirQualityApi(TestSettings.apiBasePath)
    }
}