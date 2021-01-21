package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.FavouritesApi
import fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation
import fi.metatavu.megasense.dataportal.api.translate.FavouritesTranslator
import fi.metatavu.megasense.dataportal.favourites.FavouritesController
import java.util.*
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Endpoints for favourite locations API
 */
@Stateful
@RequestScoped
class FavouritesApiImpl: FavouritesApi, AbstractApi() {
    @Inject
    private lateinit var favouritesController: FavouritesController

    @Inject
    private lateinit var favouritesTranslator: FavouritesTranslator

    override fun createUserFavouriteLocation(favouries: FavouriteLocation): Response {
        return createOk(favouritesTranslator.translate(favouritesController.createFavourite(favouries.name, favouries.latitude, favouries.longitude, loggerUserId!!)))
    }

    override fun deleteUserFavouriteLocation(favouriteId: UUID): Response {
        val favourite = favouritesController.findFavourite(favouriteId) ?: return createBadRequest("Route not found!")
        if (!favourite.creatorId!!.equals(loggerUserId!!)) {
            return createNotFound("Route not found!")
        }
        favouritesController.deleteFavourite(favourite, loggerUserId!!)
        return createNoContent()
    }

    override fun updateUserFavouriteLocation(favouriteId: UUID, favouriteLocation: FavouriteLocation): Response {
        val userId = loggerUserId!!
        val updatedFavouriteLocation = favouritesController.updateFavourite(
                favouriteId,
                favouriteLocation?.name,
                favouriteLocation?.latitude,
                favouriteLocation?.longitude,
                userId
        )

        return createOk(favouritesTranslator.translate(updatedFavouriteLocation))
    }

    override fun listUserFavouriteLocations(): Response {
        return createOk(favouritesTranslator.translate(favouritesController.listFavourites(loggerUserId!!)))
    }
}