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
            val route = testBuilder.admin().routes().create("TEST_STRING")
            val startedAt = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            val endedAt = OffsetDateTime.now().toString().replace("+03:00", "Z")
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
            assertEquals(startedAt.subSequence(IntRange(0, startedAt.indexOf("."))), exposureInstance.startedAt!!.subSequence(IntRange(0, exposureInstance.startedAt!!.indexOf("."))))
            assertEquals(endedAt.subSequence(IntRange(0, endedAt.indexOf("."))), exposureInstance.endedAt!!.subSequence(IntRange(0, exposureInstance.endedAt!!.indexOf("."))))
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
            testBuilder.admin().exposureInstances().create(null, null, null, null, null, null, null, null, null)
            Thread.sleep(2000)
            val date1 = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            Thread.sleep(2000)
            testBuilder.admin().exposureInstances().create(null, null, null, null, null, null, null, null, null)
            Thread.sleep(2000)
            val date2 = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            Thread.sleep(2000)
            testBuilder.admin().exposureInstances().create(null, null, null, null, null, null, null, null, null)

            val exposureInstanceList = testBuilder.admin().exposureInstances().list(null, null)
            assertNotNull(exposureInstanceList)
            assertTrue(exposureInstanceList.isNotEmpty())

            val exposureInstanceList2 = testBuilder.admin().exposureInstances().list(date2, null)
            assertTrue(exposureInstanceList.size >  exposureInstanceList2.size)

            val exposureInstanceList3 = testBuilder.admin().exposureInstances().list(date2, date1)
            assertTrue(exposureInstanceList2.size >  exposureInstanceList3.size)
        }
    }

    @Test
    fun testGetTotalExposure () {
        TestBuilder().use { testBuilder ->
            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    70f,
                    20f,
                    30f,
                    40f,
                    50f,
                    6000f
            )

            Thread.sleep(2000)
            val date1 = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            Thread.sleep(2000)

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    10f,
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
                    10f,
                    20f,
                    30f,
                    40f,
                    50f,
                    60f
            )

            Thread.sleep(2000)
            val date2 = OffsetDateTime.now().minusHours(3).toString().replace("+03:00", "Z")
            Thread.sleep(2000)

            testBuilder.admin().exposureInstances().create(
                    null,
                    null,
                    null,
                    20f,
                    20f,
                    30f,
                    40f,
                    50f,
                    6000f
            )

            val totalExposure = testBuilder.admin().totalExposure().get(date2, date1)
            assertEquals(20f, totalExposure.carbonMonoxide)
            assertEquals(40f, totalExposure.nitrogenMonoxide)
            assertEquals(60f, totalExposure.nitrogenDioxide)
            assertEquals(80f, totalExposure.ozone)
            assertEquals(100f, totalExposure.sulfurDioxide)
            assertEquals(120f, totalExposure.harmfulMicroparticles)
        }
    }
}