package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.UserSettings
import fi.metatavu.megasense.dataportal.api.client.apis.UserSettingsApi
import fi.metatavu.megasense.dataportal.api.client.models.HomeAddress
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings

class UserSettingsTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<UserSettings, ApiClient> (testBuilder, apiClient) {
    override fun clean(t: UserSettings?) {
        TODO("Not yet implemented")
    }

    override fun getApi(): UserSettingsApi{
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return UserSettingsApi(TestSettings.apiBasePath)
    }

    /**
     * Sends a request to create new user settings
     *
     * @param streetAddress street address
     * @param postalCode postal code
     * @param city city
     * @param country country
     *
     * @return created user settings
     */
    fun create (streetAddress: String, postalCode: String, city: String, country: String): UserSettings {
        val homeAddress = HomeAddress(streetAddress, postalCode, city, country)
        val userSettings = UserSettings(homeAddress)
        return api.createUserSettings(userSettings)
    }

    /**
     * Sends a request to update user settings
     *
     * @param streetAddress new street address
     * @param postalCode new postal code
     * @param city new city
     * @param country new country
     *
     * @return updated user settings
     */
    fun update (streetAddress: String, postalCode: String, city: String, country: String): UserSettings {
        val homeAddress = HomeAddress(streetAddress, postalCode, city, country)
        val userSettings = UserSettings(homeAddress)
        return api.updateUserSettings(userSettings)
    }

    /**
     * Sends a request user settings
     *
     * @return user settings
     */
    fun get (): UserSettings {
        return api.getUserSettings()
    }
}