package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.client.models.*
import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import fi.metatavu.megasense.dataportal.api.test.functional.resources.KeycloakResource
import fi.metatavu.megasense.dataportal.api.test.functional.resources.MysqlResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

/**
 * User tests
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(KeycloakResource::class)
)
class UserTestsIT: AbstractFunctionalTest() {

    @Test
    fun userSettingsTest() {
        TestBuilder().use { testBuilder ->
            val streetAddress = "Mutatie 9"
            val postalCode = "50708"
            val city = "Mutala"
            val country = "Suomi"

            val pollutantPenalties = PollutantPenalties(
                carbonMonoxidePenalty = 1f,
                nitrogenMonoxidePenalty = 1f,
                nitrogenDioxidePenalty = 1f,
                ozonePenalty = 1f,
                sulfurDioxidePenalty = 1f,
                harmfulMicroparticlesPenalty = 1f
            )

            val pollutantThresholds = PollutantThresholds(
                carbonMonoxideThreshold = 2f,
                nitrogenMonoxideThreshold = 2f,
                nitrogenDioxideThreshold = 2f,
                ozoneThreshold = 2f,
                sulfurDioxideThreshold = 2f,
                harmfulMicroparticlesThreshold = 2f
            )

            val medicalConditions = MedicalConditions(
                asthma = true,
                ihd = false,
                copd = false
            )

            val homeAddress = HomeAddress(streetAddress, postalCode, city, country)

            val createdSettings = testBuilder.admin().users().createUserSettings(
                homeAddress = homeAddress,
                pollutantPenalties = pollutantPenalties,
                pollutantThresholds = pollutantThresholds,
                showMobileWelcomeScreen = false,
                medicalConditions = medicalConditions
            )

            assertNotNull(createdSettings)
            assertEquals(streetAddress, createdSettings.homeAddress?.streetAddress)
            assertEquals(postalCode, createdSettings.homeAddress?.postalCode)
            assertEquals(city, createdSettings.homeAddress?.city)
            assertEquals(country, createdSettings.homeAddress?.country)
            assertEquals(false, createdSettings.showMobileWelcomeScreen)
            assertPollutantPenaltiesEqual(pollutantPenalties, createdSettings.pollutantPenalties)
            assertPollutantThresholdsEqual(pollutantThresholds, createdSettings.pollutantThresholds)
            assertTrue(createdSettings.medicalConditions.asthma)
            assertFalse(createdSettings.medicalConditions.ihd)
            assertFalse(createdSettings.medicalConditions.copd)

            val foundSettings = testBuilder.admin().users().getUserSettings()

            assertNotNull(foundSettings)
            assertEquals(streetAddress, foundSettings.homeAddress?.streetAddress)
            assertEquals(postalCode, foundSettings.homeAddress?.postalCode)
            assertEquals(city, foundSettings.homeAddress?.city)
            assertEquals(country, foundSettings.homeAddress?.country)
            assertEquals(false, foundSettings.showMobileWelcomeScreen)
            assertPollutantPenaltiesEqual(pollutantPenalties, foundSettings.pollutantPenalties)
            assertPollutantThresholdsEqual(pollutantThresholds, foundSettings.pollutantThresholds)
            assertTrue(foundSettings.medicalConditions.asthma)
            assertFalse(foundSettings.medicalConditions.ihd)
            assertFalse(foundSettings.medicalConditions.copd)

            val updatedPollutantPenalties = PollutantPenalties(
                carbonMonoxidePenalty = 3f,
                nitrogenMonoxidePenalty = 3f,
                nitrogenDioxidePenalty = 3f,
                ozonePenalty = 3f,
                sulfurDioxidePenalty = 3f,
                harmfulMicroparticlesPenalty = 3f
            )

            val updatedPollutantThresholds = PollutantThresholds(
                carbonMonoxideThreshold = 4f,
                nitrogenMonoxideThreshold = 4f,
                nitrogenDioxideThreshold = 4f,
                ozoneThreshold = 4f,
                sulfurDioxideThreshold = 4f,
                    harmfulMicroparticlesThreshold = 4f
            )

            val updatedMedicalConditions = MedicalConditions(
                asthma = true,
                ihd = true,
                copd = false
            )

            val newHomeAddress = HomeAddress( "Kuratie 19", "70898", "Kurala", "Suomaa")

            val updatedSettings = testBuilder.admin().users().updateUserSettings(
                homeAddress = newHomeAddress,
                pollutantPenalties = updatedPollutantPenalties,
                pollutantThresholds = updatedPollutantThresholds,
                showMobileWelcomeScreen = true,
                medicalConditions = updatedMedicalConditions
            )

            assertNotNull(updatedSettings)
            assertEquals(newHomeAddress.streetAddress, updatedSettings.homeAddress?.streetAddress)
            assertEquals(newHomeAddress.postalCode, updatedSettings.homeAddress?.postalCode)
            assertEquals(newHomeAddress.city, updatedSettings.homeAddress?.city)
            assertEquals(newHomeAddress.country, updatedSettings.homeAddress?.country)
            assertEquals(true, updatedSettings.showMobileWelcomeScreen)
            assertPollutantPenaltiesEqual(updatedPollutantPenalties, updatedSettings.pollutantPenalties)
            assertPollutantThresholdsEqual(updatedPollutantThresholds, updatedSettings.pollutantThresholds)
            assertTrue(updatedSettings.medicalConditions.asthma)
            assertTrue(updatedSettings.medicalConditions.ihd)
            assertFalse(updatedSettings.medicalConditions.copd)
        }
    }

    @Test
    fun userDataDownloadTest() {
        TestBuilder().use { testBuilder ->
            val route = testBuilder.admin().routes().create("routeName1","TEST_STRING", "Mikkeli", "Hirvensalmi")
            val route2 = testBuilder.admin().routes().create("routeName2","TEST_STRINGGG", "Otava", "Ristiina")

            val startedAt = OffsetDateTime.now().minusHours(3).toString().replace("+02:00", "Z").replace("+03:00", "Z")
            val endedAt = OffsetDateTime.now().toString().replace("+02:00", "Z").replace("+03:00", "Z")

            val startedAt2 = OffsetDateTime.now().minusHours(9).toString().replace("+02:00", "Z").replace("+03:00", "Z")
            val endedAt2 = OffsetDateTime.now().minusHours(6).toString().replace("+02:00", "Z").replace("+03:00", "Z")

            val exposureInstance = testBuilder.admin().exposureInstances().create(
                    routeId = route.id!!,
                    startedAt = startedAt,
                    endedAt = endedAt,
                    carbonMonoxide = 100f,
                    nitrogenMonoxide = 20f,
                    nitrogenDioxide = 30f,
                    ozone = 40f,
                    sulfurDioxide = 50f,
                    harmfulMicroparticles = 60f
            )

            val exposureInstance2 = testBuilder.admin().exposureInstances().create(
                    routeId = route2.id!!,
                    startedAt = startedAt2,
                    endedAt = endedAt2,
                    carbonMonoxide = 90f,
                    nitrogenMonoxide = 10f,
                    nitrogenDioxide = 20f,
                    ozone = 30f,
                    sulfurDioxide = 40f,
                    harmfulMicroparticles = 50f
            )

            val zipFile = testBuilder.admin().users().downloadUserData()
            assertNotNull(zipFile)

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
     * Asserts that pollutant penalties equal
     *
     * @param expected expected pollutant penalties
     * @param actual actual pollutant penalties
     */
    private fun assertPollutantPenaltiesEqual (expected: PollutantPenalties, actual: PollutantPenalties) {
        assertEquals(expected.carbonMonoxidePenalty, actual.carbonMonoxidePenalty)
        assertEquals(expected.nitrogenMonoxidePenalty, actual.nitrogenMonoxidePenalty)
        assertEquals(expected.nitrogenDioxidePenalty, actual.nitrogenDioxidePenalty)
        assertEquals(expected.ozonePenalty, actual.ozonePenalty)
        assertEquals(expected.sulfurDioxidePenalty, actual.sulfurDioxidePenalty)
        assertEquals(expected.harmfulMicroparticlesPenalty, actual.harmfulMicroparticlesPenalty)
    }

    /**
     * Asserts that pollutant penalties equal
     *
     * @param expected expected pollutant penalties
     * @param actual actual pollutant penalties
     */
    private fun assertPollutantThresholdsEqual (expected: PollutantThresholds, actual: PollutantThresholds) {
        assertEquals(expected.carbonMonoxideThreshold, actual.carbonMonoxideThreshold)
        assertEquals(expected.nitrogenMonoxideThreshold, actual.nitrogenMonoxideThreshold)
        assertEquals(expected.nitrogenDioxideThreshold, actual.nitrogenDioxideThreshold)
        assertEquals(expected.ozoneThreshold, actual.ozoneThreshold)
        assertEquals(expected.sulfurDioxideThreshold, actual.sulfurDioxideThreshold)
        assertEquals(expected.harmfulMicroparticlesThreshold, actual.harmfulMicroparticlesThreshold)
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
        assertCsvDatesEquals(exposureInstance.startedAt, csvRow[1])
        assertCsvDatesEquals(exposureInstance.endedAt, csvRow[2])
        assertEquals(exposureInstance.carbonMonoxide.toString(), csvRow[3])
        assertEquals(exposureInstance.nitrogenMonoxide.toString(), csvRow[4])
        assertEquals(exposureInstance.nitrogenDioxide.toString(), csvRow[5])
        assertEquals(exposureInstance.ozone.toString(), csvRow[6])
        assertEquals(exposureInstance.sulfurDioxide.toString(), csvRow[7])
        assertEquals(exposureInstance.harmfulMicroparticles.toString(), csvRow[8])
    }

    /**
     * Asserts that two iso dates equals
     *
     * @param expected expected
     * @param actual actual
     */
    private fun assertCsvDatesEquals(expected: String?, actual: String) {
        val expectedDateTime = OffsetDateTime.parse(expected)
        val actualDateTime = OffsetDateTime.parse(actual)
        assertEquals(expectedDateTime.toInstant().epochSecond, actualDateTime.toInstant().epochSecond)
    }
}
