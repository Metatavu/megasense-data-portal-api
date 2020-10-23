package fi.metatavu.megasense.dataportal.users

import fi.metatavu.megasense.dataportal.persistence.dao.UserSettingsDAO
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller for user settings
 */
@ApplicationScoped
class UserSettingsController {
    @Inject
    private lateinit var userSettingsDAO: UserSettingsDAO

    /**
     * Creates user settings
     *
     * @param streetAddress street address
     * @param postalCode postal code
     * @param city city
     * @param country country
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun createUserSettings (streetAddress: String?, postalCode: String?, city: String?, country: String?, creatorId: UUID): UserSettings {
        return userSettingsDAO.create(UUID.randomUUID(), streetAddress, postalCode, city, country, creatorId)
    }

    /**
     * Finds user settings belonging to a specific user
     *
     * @param userId user id
     *
     * @return user settings
     */
    fun findUserSettings (userId: UUID): UserSettings? {
        return userSettingsDAO.findByUserId(userId)
    }

    /**
     * Updates the settings of an user
     *
     * @param userSettings user settings to update
     * @param streetAddress new street address
     * @param postalCode new postal code
     * @param city new city
     * @param country new country
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateUserSettings (userSettings: UserSettings, streetAddress: String?, postalCode: String?, city: String?, country: String?, modifierId: UUID): UserSettings {
        userSettingsDAO.updateStreetAddress(userSettings, streetAddress, modifierId)
        userSettingsDAO.updatePostalCode(userSettings, postalCode, modifierId)
        userSettingsDAO.updateCity(userSettings, city, modifierId)
        userSettingsDAO.updateCountry(userSettings, country, modifierId)

        return userSettings
    }

    /**
     * Deletes user settings if they exist
     *
     * @param userId id of the user
     */
    fun deleteUserSettings (userId: UUID) {
        val userSettings = findUserSettings(userId)
        if (userSettings != null) {
            userSettingsDAO.delete(userSettings)
        }
    }
}