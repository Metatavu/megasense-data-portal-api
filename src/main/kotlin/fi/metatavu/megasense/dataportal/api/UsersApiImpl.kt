package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.api.spec.UsersApi
import fi.metatavu.megasense.dataportal.api.spec.model.UserSettings
import fi.metatavu.megasense.dataportal.api.translate.UserSettingsTranslator
import fi.metatavu.megasense.dataportal.users.UsersController
import javax.ejb.Stateful
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * Endpoints to control users
 */
@Stateful
@RequestScoped
class UsersApiImpl: UsersApi, AbstractApi() {
    @Inject
    private lateinit var usersController: UsersController

    @Inject
    private lateinit var userSettingsTranslator: UserSettingsTranslator

    override fun createUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId)
        if (foundUserSettings != null) {
            return createBadRequest("User settings already exist. Use a PUT-request to update user settings.")
        }

        val homeAddress = userSettings.homeAddress
        val createdUserSettings = usersController.createUserSettings(
                homeAddress?.streetAddress,
                homeAddress?.postalCode,
                homeAddress?.city,
                homeAddress?.country,
                userSettings.showMobileWelcomeScreen,
                userSettings.pollutantPenalties,
                userSettings.pollutantThresholds,
                userId
        )
        return createOk(userSettingsTranslator.translate(createdUserSettings))
    }

    override fun getUserSettings(): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound("User settings not found!")
        return createOk(userSettingsTranslator.translate(foundUserSettings))
    }

    override fun updateUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound("User settings not found!")

        val homeAddress = userSettings.homeAddress
        val updatedUserSettings = usersController.updateUserSettings(
                foundUserSettings,
                homeAddress?.streetAddress,
                homeAddress?.postalCode,
                homeAddress?.city,
                homeAddress?.country,
                userSettings.showMobileWelcomeScreen,
                userSettings.pollutantPenalties,
                userSettings.pollutantThresholds,
                userId
        )

        return createOk(userSettingsTranslator.translate(updatedUserSettings))
    }

    override fun deleteUser(): Response {
        usersController.deleteUser(loggerUserId!!)
        return createNoContent()
    }

    override fun deleteUserSettings(): Response {
        usersController.deleteUserSettings(loggerUserId!!)
        return createNoContent()
    }

    override fun downloadUserData(): Response {
        return streamResponse(usersController.findUserData(loggerUserId!!), "application/zip")
    }
}