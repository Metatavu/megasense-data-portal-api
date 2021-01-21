package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.persistence.model.Favourites_
import fi.metatavu.megasense.dataportal.persistence.model.Favourites
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for Favourite locations
 */
@ApplicationScoped
class FavouritesDAO: AbstractDAO<Favourites>() {
    /**
     * Creates favourite locations
     *
     * @param id UUID for identification
     * @param name location name
     * @param latitude location latitude
     * @param longitude location longitude
     * @param creatorId id of the user to whom these favourite locations belong
     *
     * @return created favourite location
     */
    fun create (id: UUID, name: String?, latitude: Float?, longitude: Float?, creatorId: UUID): Favourites {
        val favourites = Favourites()
        favourites.id = id
        favourites.name = name
        favourites.latitude = latitude
        favourites.longitude = longitude
        favourites.creatorId = creatorId
        favourites.lastModifierId = creatorId

        return persist(favourites)
    }

    /**
     * Updates favourite locations name
     *
     * @param userSettings user settings to update
     * @param name location name
     * @param modifierId id of the user to whom these favourite locations belong
     *
     * @return updated favourite location
     */
    fun updateFavouritesName (favourites: Favourites, name: String?, modifierId: UUID): Favourites {
        favourites.name = name
        favourites.lastModifierId = modifierId
        return persist(favourites)
    }

    /**
     * Updates favourite locations coordinates
     *
     * @param userSettings user settings to update
     * @param latitude new latitude
     * @param longitude new longitude
     * @param modifierId id of the user to whom these favourite locations belong
     *
     * @return updated favourite location
     */
    fun updateFavouritesCoordinates (favourites: Favourites, latitude: Float?, longitude: Float?, modifierId: UUID): Favourites {
        favourites.latitude = latitude
        favourites.longitude = longitude
        favourites.lastModifierId = modifierId
        return persist(favourites)
    }

    /**
     * Finds favourite locations belonging to a specific user
     *
     * @param userId user id
     *
     * @return favourite locations
     */
    fun list(userId: UUID): List<Favourites> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Favourites::class.java)
        val root = criteria.from(Favourites::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(Favourites_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()));
        criteria.orderBy(criteriaBuilder.desc(root.get(Favourites_.createdAt)))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}