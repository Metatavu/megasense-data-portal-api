package fi.metatavu.megasense.dataportal.favourites

import fi.metatavu.megasense.dataportal.persistence.dao.FavouriteDAO
import fi.metatavu.megasense.dataportal.persistence.model.Favourite
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller class for favourite locations
 */
@ApplicationScoped
class FavouritesController {
    @Inject
    private lateinit var favouriteDAO: FavouriteDAO

    /**
     * Creates a favourite location
     *
     * @param name A string representing favourite location name
     * @param latitude string latitude of a favourite location
     * @param longitude string latitude of a favourite location
     * @param creatorId id of the user who created this favourite location
     *
     * @return created favourite location
     */
    fun createFavourite (name: String, latitude: Float, longitude: Float, creatorId: UUID): Favourite {
        return favouriteDAO.create(UUID.randomUUID(), name, latitude, longitude, creatorId)
    }

    /**
     * Lists all favourite locations
     *
     * @param userId id of the user to whom the favourite locations belong
     *
     * @return favourite locations
     */
    fun listFavourites (userId: UUID): List<Favourite> {
        return favouriteDAO.list(userId)
    }

    /**
     * Finds a favourite location
     *
     * @param favouriteId id of a favourite location to find
     *
     * @return found favourite location or null if not found
     */
    fun findFavourite (favouriteId: UUID): Favourite? {
        return favouriteDAO.findById(favouriteId)
    }

    /**
     * Updates a favourite location
     *
     * @param favouriteId id of a favourite location to update
     *
     * @return updated favourite location or null if not found
     */
    fun updateFavourite (favouriteId: UUID, name: String, latitude: Float, longitude: Float, modifierId: UUID): Favourite {
        val foundFavourite = favouriteDAO.findById(favouriteId)!!
        favouriteDAO.updateFavouritesName(foundFavourite, name, modifierId)
        favouriteDAO.updateFavouritesCoordinates(foundFavourite, latitude, longitude, modifierId)
        return foundFavourite
    }

    /**
     * Deletes a favourite location
     *
     * @param favourite a favourite location to delete
     * @param userId id of the user who is deleting this favourite location
     */
    fun deleteFavourite (favourite: Favourite, userId: UUID) {
        favouriteDAO.delete(favourite)
    }
}