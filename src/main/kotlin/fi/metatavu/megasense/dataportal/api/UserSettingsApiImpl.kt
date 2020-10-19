package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.UserSettingsApi
import fi.metatavu.megasense.dataportal.api.spec.model.UserSettings
import fi.metatavu.megasense.dataportal.api.translate.UserSettingsTranslator
import fi.metatavu.megasense.dataportal.usersettings.UserSettingsController
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * The REST endpoint for total exposure
 */
@RequestScoped
@Stateful
class UserSettingsApiImpl: UserSettingsApi, AbstractApi() {
    @Inject
    private lateinit var userSettingsController: UserSettingsController
    @Inject
    private lateinit var userSettingsTranslator: UserSettingsTranslator

    override fun createUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = userSettingsController.findUserSettings(userId)
        if (foundUserSettings != null) {
            return createBadRequest("User settings already exist. Use a PUT-request to update user settings.")
        }

        val createdUserSettings = userSettingsController.createUserSettings(userSettings.homeAddress, userId)
        return createOk(userSettingsTranslator.translate(createdUserSettings))
    }

    override fun getUserSettings(): Response {
        val userId = loggerUserId!!
        val foundUserSettings = userSettingsController.findUserSettings(userId) ?: return createNotFound("User settings not found!")
        return createOk(userSettingsTranslator.translate(foundUserSettings))
    }

    override fun updateUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = userSettingsController.findUserSettings(userId) ?: return createNotFound("User settings not found!")
        val updatedUserSettings = userSettingsController.updateUserSettings(foundUserSettings, userSettings.homeAddress, userId)
        return createOk(userSettingsTranslator.translate(updatedUserSettings))
    }
}