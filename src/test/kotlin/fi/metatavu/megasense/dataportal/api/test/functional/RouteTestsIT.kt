package fi.metatavu.megasense.dataportal.api.test.functional

import fi.metatavu.megasense.dataportal.api.test.functional.builder.AbstractFunctionalTest
import fi.metatavu.megasense.dataportal.api.test.functional.builder.TestBuilder
import fi.metatavu.megasense.dataportal.api.test.functional.resources.KeycloakResource
import fi.metatavu.megasense.dataportal.api.test.functional.resources.MysqlResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * A test class for routes
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(KeycloakResource::class)
)
class RouteTestsIT: AbstractFunctionalTest() {
    @Test
    fun testCreateRoute () {
        TestBuilder().use { testBuilder ->
            val route = testBuilder.admin().routes().create("my route","TEST_STRING", "Mikkeli", "Hirvensalmi")
            assertNotNull(route)
            assertEquals("my route", route.name)
            assertEquals("TEST_STRING", route.routePoints)
            assertEquals("Mikkeli", route.locationFromName)
            assertEquals("Hirvensalmi", route.locationToName)
            assertNotNull(route.savedAt)
        }
    }

    @Test
    fun testFindRoute () {
        TestBuilder().use { testBuilder ->
            val routeId = testBuilder.admin().routes().create("routeName1", "TEST_STRING", "Mikkeli", "Hirvensalmi").id!!
            val foundRoute = testBuilder.admin().routes().find(routeId)
            assertNotNull(foundRoute)
        }
    }

    @Test
    fun testListRoutes () {
        TestBuilder().use { testBuilder ->
            testBuilder.admin().routes().create("routeName1", "TEST_STRING", "Mikkeli", "Hirvensalmi")
            testBuilder.admin().routes().create("routeName2", "TEST_STRING", "Mikkeli", "Hirvensalmi")
            testBuilder.admin().routes().create("routeName3", "TEST_STRING", "Mikkeli", "Hirvensalmi")

            val routeList = testBuilder.admin().routes().listAll()
            assertNotNull(routeList)
            assertEquals(3, routeList.size)
        }
    }
}