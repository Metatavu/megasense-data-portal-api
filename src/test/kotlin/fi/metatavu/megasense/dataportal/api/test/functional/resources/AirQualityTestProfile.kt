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
        config["AIR_QUALITY_TIME"] = "time"
        config["AIR_QUALITY_LAT"] = "lat"
        config["AIR_QUALITY_LON"] = "lon"
        config["AIR_QUALITY_DAYMAX_PM10"] = "daymax_cnc_PM10"
        config["AIR_QUALITY_DAYMAX_PM2_5"] = "daymax_cnc_PM2_5"
        config["CARBON_MONOXIDE"] = "daymax_cnc_CO"
        config["NITROGEN_MONOXIDE"] = "daymax_cnc_NO"
        config["NITROGEN_DIOXIDE"] = "daymax_cnc_NO2"
        config["OZONE"] = "daymax_cnc_O3"
        config["SULFUR_DIOXIDE"] = "daymax_cnc_SO2"
        return config
    }

    override fun getConfigProfile(): String {
        return "test-profile"
    }
}
