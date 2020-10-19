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
     * @param homeAddress home address
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun create (id: UUID, homeAddress: String?, creatorId: UUID): UserSettings {
        val userSettings = UserSettings()
        userSettings.id = id
        userSettings.homeAddress = homeAddress
        userSettings.creatorId = creatorId
        userSettings.lastModifierId = creatorId

        return persist(userSettings)
    }

    /**
     * Updates the home address of an user
     *
     * @param userSettings user settings to update
     * @param homeAddress new home address
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateHomeAddress (userSettings: UserSettings, homeAddress: String?, modifierId: UUID): UserSettings {
        userSettings.homeAddress = homeAddress
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