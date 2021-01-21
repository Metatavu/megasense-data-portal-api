package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.persistence.model.Favourite
import javax.enterprise.context.ApplicationScoped

/**
 * A translator class for favourite locations
 */
@ApplicationScoped
class FavouritesTranslator: AbstractTranslator<fi.metatavu.megasense.dataportal.persistence.model.Favourite, fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation>() {
    /**
     * Translates JPA favourites into REST favourites location
     *
     * @param entity JPA favourite location
     *
     * @return REST favourite location
     */
    override fun translate(entity: Favourite): fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation {
        val favourite = fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation()
        favourite.id = entity.id
        favourite.name = entity.name
        favourite.latitude = entity.latitude
        favourite.longitude = entity.longitude
        return favourite
    }
}