package fi.metatavu.megasense.dataportal.usersettings

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
     * @param homeAddress home address
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun createUserSettings (homeAddress: String?, creatorId: UUID): UserSettings {
        return userSettingsDAO.create(UUID.randomUUID(), homeAddress, creatorId)
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
     * @param homeAddress new home address
     * @param modifierId id of the user to whom these settings belong
     *
     * @return updated user settings
     */
    fun updateUserSettings (userSettings: UserSettings, homeAddress: String?, modifierId: UUID): UserSettings {
        return userSettingsDAO.updateHomeAddress(userSettings, homeAddress, modifierId)
    }
}