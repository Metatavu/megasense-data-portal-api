package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import org.junit.Assert.*
import org.junit.Test

/**
 * A test class for routes
 */
class FavouritesTestsIT: AbstractFunctionalTest() {
    @Test
    fun testCreateFavourite () {
        TestBuilder().use { testBuilder ->
            val favourite = testBuilder.admin().favourites().create("Name", 20.7182818284f, 60.7182818284f)
            assertNotNull(favourite)
            assertEquals("Name", favourite.name)
            assertEquals(20.7182818284f, favourite.latitude)
            assertEquals(60.7182818284f, favourite.longitude)
            //assertNotNull(favourite.id)
        }
    }

//    @Test
//    fun testFindRoute () {
//        TestBuilder().use { testBuilder ->
//            val routeId = testBuilder.admin().routes().create("Name", "TEST_STRING", "Mikkeli", "Hirvensalmi").id!!
//            val foundRoute = testBuilder.admin().routes().find(routeId)
//            assertNotNull(foundRoute)
//        }
//    }
//
//    @Test
//    fun testListRoutes () {
//        TestBuilder().use { testBuilder ->
//            testBuilder.admin().routes().create("Name", "TEST_STRING", "Mikkeli", "Hirvensalmi")
//            testBuilder.admin().routes().create("Name", "TEST_STRING", "Mikkeli", "Hirvensalmi")
//            testBuilder.admin().routes().create("Name", "TEST_STRING", "Mikkeli", "Hirvensalmi")
//
//            val routeList = testBuilder.admin().routes().listAll()
//            assertNotNull(routeList)
//            assertEquals(3, routeList.size)
//        }
//    }
}