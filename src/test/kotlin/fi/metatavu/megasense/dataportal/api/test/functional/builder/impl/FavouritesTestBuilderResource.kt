package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.FavouriteLocationsApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.FavouriteLocation
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import java.util.*

/**
 * Test builder resource for handling favourites
 */
class FavouritesTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<FavouriteLocation, ApiClient> (testBuilder, apiClient) {
    /**
     * Sends a request to create a favourite
     *
     * @param name name of a favourite
     * @param latitude latitude of a favourite
     * @param longitude longitude of a favourite
     *
     * @return created favourite
     */
    fun create (name: String, latitude: Float, longitude: Float): FavouriteLocation {
        val favourite = FavouriteLocation(name, latitude, longitude, UUID.randomUUID())
        return addClosable(api.createUserFavouriteLocation(favourite))
    }

    /**
     * Sends a request to list all favourites
     *
     * @return all favourites created by the user
     */
    fun listAll (): Array<FavouriteLocation> {
        return api.listUserFavouriteLocations()
    }

    /**
     * Sends a request to update a favourite
     *
     * @param favouriteId id of the favourite to update
     * @param name name of a favourite
     * @param latitude latitude of a favourite
     * @param longitude longitude of a favourite
     * @return updated favourite
     */
    fun update (favouriteId: UUID, name: String, latitude: Float, longitude: Float): FavouriteLocation {
        val favourite = FavouriteLocation(name, latitude, longitude)
        return api.updateUserFavouriteLocation(favouriteId, favourite)
    }

    override fun clean (favourite: FavouriteLocation) {
        api.deleteUserFavouriteLocation(favourite.id!!)
    }

    override fun getApi (): FavouriteLocationsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return FavouriteLocationsApi(TestSettings.apiBasePath)
    }
}