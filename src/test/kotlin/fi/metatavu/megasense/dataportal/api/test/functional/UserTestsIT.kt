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

            val updatedSettings = testBuilder.admin().users().updateUserSettings("Kuratie 19", "70898", "Kurala", "Suomaa")
            assertNotNull(updatedSettings)
            assertEquals("Kuratie 19", updatedSettings.homeAddress?.streetAddress)
            assertEquals("70898", updatedSettings.homeAddress?.postalCode)
            assertEquals("Kurala", updatedSettings.homeAddress?.city)
            assertEquals("Suomaa", updatedSettings.homeAddress?.country)
        }
    }

    @Test
    fun userDataDownloadTest() {
        TestBuilder().use { testBuilder ->
            val route = testBuilder.admin().routes().create("TEST_STRING", "Mikkeli", "Hirvensalmi")
            val route2 = testBuilder.admin().routes().create("TEST_STRINGGG", "Otava", "Ristiina")

            val startedAt = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            val endedAt = OffsetDateTime.now().toString().replace("+03:00", "Z")

            val startedAt2 = OffsetDateTime.now().minusHours(9).toString().replace("+03:00", "Z")
            val endedAt2 = OffsetDateTime.now().minusHours(6).toString().replace("+03:00", "Z")

           val exposureInstance = testBuilder.admin().exposureInstances().create(
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

            val exposureInstance2 = testBuilder.admin().exposureInstances().create(
                    route2.id!!,
                    startedAt2,
                    endedAt2,
                    90f,
                    10f,
                    20f,
                    30f,
                    40f,
                    50f
            )

            val zipFile = testBuilder.admin().users().downloadUserData()
            val exposureLines = readLinesFromZipEntry(zipFile, "exposure.csv")
            val routeLines = readLinesFromZipEntry(zipFile, "routes.csv")
            zipFile.delete()

            val exposureHeader = exposureLines[0].split(",")
            val firstExposureEntry = exposureLines[1].split(",")
            val secondExposureEntry = exposureLines[2].split(",")

            val routeHeader = routeLines[0].split(",")
            val firstRouteEntry = routeLines[1].split(",")
            val secondRouteEntry = routeLines[2].split(",")

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

            assertCorrectCsvRow(route2, firstRouteEntry)
            assertCorrectCsvRow(route, secondRouteEntry)

            assertCorrectCsvRow(exposureInstance2, firstExposureEntry)
            assertCorrectCsvRow(exposureInstance, secondExposureEntry)
        }
    }

    /**
     * Asserts that a row contains correct data
     * Null values not supported by this test
     *
     * @param route a route to test against
     * @param csvRow a row to test
     */
    private fun assertCorrectCsvRow (route: Route, csvRow: List<String>) {
        assertEquals(route.id.toString(), csvRow[0])
        assertEquals(route.routePoints, csvRow[1])
        assertEquals(route.locationFromName, csvRow[2])
        assertEquals(route.locationToName, csvRow[3])
        assertEquals(route.savedAt.toString().split(".")[0], csvRow[4].split(".")[0])
    }

    /**
     * Asserts that a row contains correct data
     * Null values not supported by this test
     *
     * @param exposureInstance exposure instance to test against
     * @param csvRow a row to test
     */
    private fun assertCorrectCsvRow (exposureInstance: ExposureInstance, csvRow: List<String>) {
        assertEquals(exposureInstance.routeId.toString(), csvRow[0])
        assertEquals(exposureInstance.startedAt.toString().split(".")[0], csvRow[1].split(".")[0])
        assertEquals(exposureInstance.endedAt.toString().split(".")[0], csvRow[2].split(".")[0])
        assertEquals(exposureInstance.carbonMonoxide.toString(), csvRow[3])
        assertEquals(exposureInstance.nitrogenMonoxide.toString(), csvRow[4])
        assertEquals(exposureInstance.nitrogenDioxide.toString(), csvRow[5])
        assertEquals(exposureInstance.ozone.toString(), csvRow[6])
        assertEquals(exposureInstance.sulfurDioxide.toString(), csvRow[7])
        assertEquals(exposureInstance.harmfulMicroparticles.toString(), csvRow[8])
    }
}