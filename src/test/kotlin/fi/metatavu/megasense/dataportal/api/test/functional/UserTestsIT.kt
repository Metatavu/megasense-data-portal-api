package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.client.models.ExposureInstance
import fi.metatavu.megasense.dataportal.api.client.models.Route
import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import org.junit.Assert.*
import org.junit.Test
import java.time.OffsetDateTime

/**
 * User tests
 */
class UserTestsIT: AbstractFunctionalTest() {
    @Test
    fun userSettingsTest() {
        TestBuilder().use { testBuilder ->
            val createdSettings = testBuilder.admin().users().createUserSettings("Mutatie 9", "50708", "Mutala", "Suomi")

            assertNotNull(createdSettings)
            assertEquals("Mutatie 9", createdSettings.homeAddress?.streetAddress)
            assertEquals("50708", createdSettings.homeAddress?.postalCode)
            assertEquals("Mutala", createdSettings.homeAddress?.city)
            assertEquals("Suomi", createdSettings.homeAddress?.country)

            val foundSettings = testBuilder.admin().users().getUserSettings()

            assertNotNull(foundSettings)
            assertEquals("Mutatie 9", foundSettings.homeAddress?.streetAddress)
            assertEquals("50708", foundSettings.homeAddress?.postalCode)
            assertEquals("Mutala", foundSettings.homeAddress?.city)
            assertEquals("Suomi", foundSettings.homeAddress?.country)

            val updatedSettings = testBuilder.admin().users().updateUserSettings("Kuratie 19", "70898", "Kurala", "Syrjälä")
            assertNotNull(updatedSettings)
            assertEquals("Kuratie 19", updatedSettings.homeAddress?.streetAddress)
            assertEquals("70898", updatedSettings.homeAddress?.postalCode)
            assertEquals("Kurala", updatedSettings.homeAddress?.city)
            assertEquals("Syrjälä", updatedSettings.homeAddress?.country)
        }
    }

    @Test
    fun userDataDownloadTest() {
        TestBuilder().use { testBuilder ->
            val route = testBuilder.admin().routes().create("TEST_STRING", "Mikkeli", "Hirvensalmi")
            val startedAt = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            val endedAt = OffsetDateTime.now().toString().replace("+03:00", "Z")

           testBuilder.admin().exposureInstances().create(
                    route.id!!,
                    startedAt,
                    endedAt,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            val zipFile = testBuilder.admin().users().downloadUserData()
            val exposureLines = readLinesFromZipEntry(zipFile, "exposure.csv")
            val routeLines = readLinesFromZipEntry(zipFile, "routes.csv")
            zipFile.delete()

            val exposureHeader = exposureLines[0].split(",")
            val routeHeader = routeLines[0].split(",")

            assertTrue(exposureLines.size > 1)
            assertTrue(routeLines.size > 1)

            assertEquals(exposureHeader[0], "Route")
            assertEquals(exposureHeader[1], "Started at")
            assertEquals(exposureHeader[2], "Ended at")
            assertEquals(exposureHeader[3], "Carbon monoxide")
            assertEquals(exposureHeader[4], "Nitrogen monoxide")
            assertEquals(exposureHeader[5], "Nitrogen dioxide")
            assertEquals(exposureHeader[6], "Ozone")
            assertEquals(exposureHeader[7], "Sulfur dioxide")
            assertEquals(exposureHeader[8], "Microparticles")

            assertEquals(routeHeader[0], "Id")
            assertEquals(routeHeader[1], "Route points")
            assertEquals(routeHeader[2], "Start location")
            assertEquals(routeHeader[3], "End location")
            assertEquals(routeHeader[4], "Saved at")
        }
    }
}