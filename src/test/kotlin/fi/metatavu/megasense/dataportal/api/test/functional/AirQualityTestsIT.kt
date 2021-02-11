package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import fi.metatavu.megasense.dataportal.api.test.functional.resources.KeycloakResource
import fi.metatavu.megasense.dataportal.api.test.functional.resources.MysqlResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.Assert.assertNotNull

/**
 * A test class for air quality
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(KeycloakResource::class)
)
class AirQualityTestsIT: AbstractFunctionalTest() {

    @Test
    fun testGetAirQuality() {
        TestBuilder().use { testBuilder ->
            val corner1 = "60,21"
            val corner2 = "65,26"

            val carbonMonoxide = testBuilder.admin().airQuality().getAirQuality("CARBON_MONOXIDE", corner1, corner2)
            assertNotNull(carbonMonoxide)

            val nitrogenMonoxide = testBuilder.admin().airQuality().getAirQuality("NITROGEN_MONOXIDE", corner1, corner2)
            assertNotNull(nitrogenMonoxide)

            val nitrogenDioxide = testBuilder.admin().airQuality().getAirQuality("NITROGEN_DIOXIDE", corner1, corner2)
            assertNotNull(nitrogenDioxide)

            val sulfurDioxide = testBuilder.admin().airQuality().getAirQuality("SULFUR_DIOXIDE", corner1, corner2)
            assertNotNull(sulfurDioxide)

            val ozone = testBuilder.admin().airQuality().getAirQuality("OZONE", corner1, corner2)
            assertNotNull(ozone)

            val microParticles = testBuilder.admin().airQuality().getAirQuality("MICRO_PARTICLES", corner1, corner2)
            assertNotNull(microParticles)

        }
    }

    @Test
    fun testGetAirQualityForCoordinates() {
        TestBuilder().use { testBuilder ->
            val coordinates = "60.20383377832825,25.03689765930176"

            val carbonMonoxide = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "CARBON_MONOXIDE")
            assertNotNull(carbonMonoxide)

            val nitrogenMonoxide = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "NITROGEN_MONOXIDE")
            assertNotNull(nitrogenMonoxide)

            val nitrogenDioxide = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "NITROGEN_DIOXIDE")
            assertNotNull(nitrogenDioxide)

            val sulfurDioxide = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "SULFUR_DIOXIDE")
            assertNotNull(sulfurDioxide)

            val ozone = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "OZONE")
            assertNotNull(ozone)

            val microParticles = testBuilder.admin().airQuality().getAirQualityForCoordinates(coordinates, "MICRO_PARTICLES")
            assertNotNull(microParticles)
        }
    }
}