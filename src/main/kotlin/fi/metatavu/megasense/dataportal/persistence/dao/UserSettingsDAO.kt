package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.persistence.model.UserSettings_
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for user settings
 */
@ApplicationScoped
class UserSettingsDAO: AbstractDAO<UserSettings>() {
    /**
     * Creates user settings
     *
     * @param id UUID for identification
     * @param streetAddress street address
     * @param postalCode postal code
     * @param city city
     * @param country country
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun create (id: UUID, streetAddress: String?, postalCode: String?, city: String?, country: String?, creatorId: UUID): UserSettings {
        val userSettings = UserSettings()
        userSettings.id = id
        userSettings.streetAddress = streetAddress
        userSettings.postalCode = postalCode
        userSettings.city = city
        userSettings.country = country
        userSettings.creatorId = creatorId
        userSettings.lastModifierId = creatorId

        return persist(userSettings)
    }

    /**
     * Updates the street address of an user
     *
     * @param userSettings user settings to update
     * @param streetAddress new street address
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateStreetAddress (userSettings: UserSettings, streetAddress: String?, modifierId: UUID): UserSettings {
        userSettings.streetAddress = streetAddress
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the postal code of an user
     *
     * @param userSettings user settings to update
     * @param postalCode new postal code
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updatePostalCode (userSettings: UserSettings, postalCode: String?, modifierId: UUID): UserSettings {
        userSettings.postalCode = postalCode
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the city of an user
     *
     * @param userSettings user settings to update
     * @param city new city
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateCity (userSettings: UserSettings, city: String?, modifierId: UUID): UserSettings {
        userSettings.city = city
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the country of an user
     *
     * @param userSettings user settings to update
     * @param country country
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateCountry (userSettings: UserSettings, country: String?, modifierId: UUID): UserSettings {
        userSettings.country = country
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Finds user settings belonging to a specific user
     *
     * @param userId user id
     *
     * @return user settings
     */
    fun findByUserId (userId: UUID): UserSettings? {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(UserSettings::class.java)
        val root = criteria.from(UserSettings::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(UserSettings_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()));

        val query = entityManager.createQuery(criteria)

        if (query.resultList.size < 1) {
            return null
        }

        return query.resultList[0]
    }
}