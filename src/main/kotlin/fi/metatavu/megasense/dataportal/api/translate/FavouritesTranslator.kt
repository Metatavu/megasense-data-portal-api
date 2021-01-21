package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.persistence.model.Favourites
import javax.enterprise.context.ApplicationScoped

/**
 * A translator class for favourite locations
 */
@ApplicationScoped
class FavouritesTranslator: AbstractTranslator<fi.metatavu.megasense.dataportal.persistence.model.Favourites, fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation>() {
    /**
     * Translates JPA favourites into REST favourites location
     *
     * @param entity JPA favourite location
     *
     * @return REST favourite location
     */
    override fun translate(entity: Favourites): fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation {
        val favourites = fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation()
        favourites.name = entity.name
        favourites.latitude = entity.latitude
        favourites.longitude = entity.longitude
        return favourites
    }
}