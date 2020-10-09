package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.airquality.AirQualityController
import fi.metatavu.megasense.dataportal.api.spec.AirQualityApi
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * An endpoint for air quality
 */
@RequestScoped
@Stateful
class AirQualityApiImpl: AirQualityApi, AbstractApi() {
    @Inject
    private lateinit var airQualityController: AirQualityController

    override fun getAirQuality(pollutant: String, precision: Int, boundingBoxCorner1: String, boundingBoxCorner2: String): Response {
        return createOk(airQualityController.getAirQuality(pollutant, precision, boundingBoxCorner1, boundingBoxCorner2))
    }
}