package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.persistence.model.Favourite
import fi.metatavu.megasense.dataportal.persistence.model.Favourite_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for Favourite locations
 */
@ApplicationScoped
class FavouritesDAO: AbstractDAO<Favourite>() {
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
    fun create (id: UUID, name: String?, latitude: Float?, longitude: Float?, creatorId: UUID): Favourite {
        val favourite = Favourite()
        favourite.id = id
        favourite.name = name
        favourite.latitude = latitude
        favourite.longitude = longitude
        favourite.creatorId = creatorId
        favourite.lastModifierId = creatorId

        return persist(favourite)
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
    fun updateFavouritesName (favourite: Favourite, name: String?, modifierId: UUID): Favourite {
        favourite.name = name
        favourite.lastModifierId = modifierId
        return persist(favourite)
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
    fun updateFavouritesCoordinates (favourite: Favourite, latitude: Float?, longitude: Float?, modifierId: UUID): Favourite {
        favourite.latitude = latitude
        favourite.longitude = longitude
        favourite.lastModifierId = modifierId
        return persist(favourite)
    }

    /**
     * Finds favourite locations belonging to a specific user
     *
     * @param userId user id
     *
     * @return favourite locations
     */
    fun list(userId: UUID): List<Favourite> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Favourite::class.java)
        val root = criteria.from(Favourite::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(Favourite_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()));
        criteria.orderBy(criteriaBuilder.desc(root.get(Favourite_.createdAt)))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}