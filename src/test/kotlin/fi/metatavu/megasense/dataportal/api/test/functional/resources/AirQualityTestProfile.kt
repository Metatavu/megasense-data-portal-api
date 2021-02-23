package fi.metatavu.megasense.dataportal.api.test.functional.resources;

import io.quarkus.test.junit.QuarkusTestProfile

/**
 * Class for air quality test profile
 *
 * @author Katja Danilova
 */
class AirQualityTestProfile: QuarkusTestProfile {

    override fun getConfigOverrides(): Map<String, String> {
        val config: MutableMap<String, String> = HashMap()
        config["megasense.airquality.parameters.AIR_QUALITY_TIME"] = "time"
        config["megasense.airquality.parameters.AIR_QUALITY_LAT"] = "lat"
        config["megasense.airquality.parameters.AIR_QUALITY_LON"] = "lon"
        config["megasense.airquality.parameters.AIR_QUALITY_DAYMAX_PM10"] = "daymax_cnc_PM10"
        config["megasense.airquality.parameters.AIR_QUALITY_DAYMAX_PM2_5"] = "daymax_cnc_PM2_5"
        config["megasense.airquality.parameters.CARBON_MONOXIDE"] = "daymax_cnc_CO"
        config["megasense.airquality.parameters.NITROGEN_MONOXIDE"] = "daymax_cnc_NO"
        config["megasense.airquality.parameters.NITROGEN_DIOXIDE"] = "daymax_cnc_NO2"
        config["megasense.airquality.parameters.OZONE"] = "daymax_cnc_O3"
        config["megasense.airquality.parameters.SULFUR_DIOXIDE"] = "daymax_cnc_SO2"

        config["megasense.airquality.path"] = "data/airquality.nc"
        return config
    }

    override fun getConfigProfile(): String {
        return "test-profile"
    }
}
