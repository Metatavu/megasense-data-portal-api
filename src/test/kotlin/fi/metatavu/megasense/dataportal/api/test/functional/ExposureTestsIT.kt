package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import org.junit.Assert.*
import org.junit.Test
import java.time.OffsetDateTime

/**
 * A test class for exposure
 */
class ExposureTestsIT: AbstractFunctionalTest() {
    @Test
    fun testCreateExposureInstance () {
        TestBuilder().use { testBuilder ->
            val route = testBuilder.admin().routes().create("Name","TEST_STRING", "Mikkeli", "Hirvensalmi")
            val startedAt = OffsetDateTime.now().minusHours(3).toString().replace("+02:00", "Z").replace("+03:00", "Z")
            val endedAt = OffsetDateTime.now().toString().replace("+02:00", "Z").replace("+03:00", "Z")
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

            assertNotNull(exposureInstance)
            assertEquals(route.id, exposureInstance.routeId)

            assertEquals(startedAt.split(".")[0], exposureInstance.startedAt!!.split(".")[0])
            assertEquals(endedAt.split(".")[0], exposureInstance.endedAt!!.split(".")[0])

            assertEquals(100f, exposureInstance.carbonMonoxide)
            assertEquals(20f, exposureInstance.nitrogenMonoxide)
            assertEquals(30f, exposureInstance.nitrogenDioxide)
            assertEquals(40f, exposureInstance.ozone)
            assertEquals(50f, exposureInstance.sulfurDioxide)
            assertEquals(60f, exposureInstance.harmfulMicroparticles)
        }
    }

    @Test
    fun testFindExposureInstance () {
        TestBuilder().use { testBuilder ->
            val exposureInstanceId = testBuilder.admin().exposureInstances().create(null, null, null, null, null, null, null, null, null).id!!
            val foundExposureInstance = testBuilder.admin().exposureInstances().find(exposureInstanceId)
            assertNotNull(foundExposureInstance)
        }
    }

    @Test
    fun testListExposureInstances () {
        TestBuilder().use { testBuilder ->
            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            val exposureInstances = testBuilder.admin().exposureInstances().list(null, null)
            assertNotNull(exposureInstances)
            assertEquals(3, exposureInstances.size)
        }
    }

    @Test
    fun testGetTotalExposure () {
        TestBuilder().use { testBuilder ->
            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    100f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            val totalExposure = testBuilder.admin().totalExposure().get(null, null)
            assertNotNull(totalExposure)

            assertEquals(300f, totalExposure.carbonMonoxide)
            assertEquals(60f, totalExposure.nitrogenMonoxide)
            assertEquals(90f, totalExposure.nitrogenDioxide)
            assertEquals(120f, totalExposure.ozone)
            assertEquals(150f, totalExposure.sulfurDioxide)
            assertEquals(180f, totalExposure.harmfulMicroparticles)
        }
    }
}