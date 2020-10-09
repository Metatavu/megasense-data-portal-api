package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AirQualityTestsIT: AbstractFunctionalTest(){
    @Test
    fun testGetAirQuality() {
        TestBuilder().use { testBuilder ->
            val corner1 = "60.15795230036145,24.860429763793945"
            val corner2 = "60.20383377832825,25.03689765930176"

            val carbonMonoxide = testBuilder.admin().airQuality().getAirQuality("CARBON_MONOXIDE", 1000, corner1, corner2)
            assertNotNull(carbonMonoxide)
            testBuilder.admin().airQuality().assertCorrectStructure(carbonMonoxide)

            val nitrogenMonoxide = testBuilder.admin().airQuality().getAirQuality("NITROGEN_MONOXIDE", 1000, corner1, corner2)
            assertNotNull(nitrogenMonoxide)
            testBuilder.admin().airQuality().assertCorrectStructure(nitrogenMonoxide)

            val nitrogenDioxide = testBuilder.admin().airQuality().getAirQuality("NITROGEN_DIOXIDE", 1000, corner1, corner2)
            assertNotNull(nitrogenDioxide)
            testBuilder.admin().airQuality().assertCorrectStructure(nitrogenDioxide)

            val sulfurDioxide = testBuilder.admin().airQuality().getAirQuality("SULFUR_DIOXIDE", 1000, corner1, corner2)
            assertNotNull(sulfurDioxide)
            testBuilder.admin().airQuality().assertCorrectStructure(sulfurDioxide)

            val ozone = testBuilder.admin().airQuality().getAirQuality("OZONE", 1000, corner1, corner2)
            assertNotNull(ozone)
            testBuilder.admin().airQuality().assertCorrectStructure(ozone)

            val microParticles = testBuilder.admin().airQuality().getAirQuality("MICRO_PARTICLES", 1000, corner1, corner2)
            assertNotNull(microParticles)
            testBuilder.admin().airQuality().assertCorrectStructure(microParticles)

        }
    }
}