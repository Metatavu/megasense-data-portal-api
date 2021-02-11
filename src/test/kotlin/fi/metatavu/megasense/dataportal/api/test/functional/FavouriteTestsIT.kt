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
 * A test class for favourites
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(KeycloakResource::class)
)
class FavouriteTestsIT: AbstractFunctionalTest() {

    @Test
    fun testCreateFavourite () {
        TestBuilder().use { testBuilder ->
            val favourite = testBuilder.admin().favourites().create("Name", 20.7182818284f, 60.7182818284f)
            assertNotNull(favourite)
            assertEquals("Name", favourite.name)
            assertEquals(20.7182818284f, favourite.latitude)
            assertEquals(60.7182818284f, favourite.longitude)
            assertNotNull(favourite.id)
        }
    }

    @Test
    fun testUpdateFavourite () {
        TestBuilder().use { testBuilder ->
            val favourite = testBuilder.admin().favourites().create("Name", 20.7182818284f, 60.7182818284f)
            val newFavourite = testBuilder.admin().favourites().update(favourite.id!!,"New Name", 21.7182818284f, 63.7182818284f)
            assertNotNull(favourite)
            assertEquals("New Name", newFavourite.name)
            assertEquals(21.7182818284f, newFavourite.latitude)
            assertEquals(63.7182818284f, newFavourite.longitude)
            assertNotNull(newFavourite.id)
        }
    }

    @Test
    fun testListFavourites () {
        TestBuilder().use { testBuilder ->
            testBuilder.admin().favourites().create("Name1", 21.7182818284f, 61.7182818284f)
            testBuilder.admin().favourites().create("Name2", 22.7182818284f, 62.7182818284f)
            testBuilder.admin().favourites().create("Name3", 23.7182818284f, 63.7182818284f)

            val favouriteList = testBuilder.admin().favourites().listAll()
            assertNotNull(favouriteList)
            assertEquals(3, favouriteList.size)
        }
    }
}