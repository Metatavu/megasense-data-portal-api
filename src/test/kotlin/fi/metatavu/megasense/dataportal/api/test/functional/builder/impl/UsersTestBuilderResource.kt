package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.UsersApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.*
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import java.io.File
import java.io.FileOutputStream

/**
 * Test builder resource for handling users
 */
class UsersTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<UserSettings, ApiClient>(testBuilder, apiClient) {
    override fun clean(userSettings: UserSettings) {
        api.deleteUserSettings()
    }

    override fun getApi(): UsersApi{
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return UsersApi(TestSettings.apiBasePath)
    }

    /**
     * Sends a request to create new user settings
     *
     * @param homeAddress home address
     * @param pollutantPenalties pollutant penalties
     * @param pollutantThresholds pollutant thresholds
     * @param showMobileWelcomeScreen a boolean setting for showing the mobile welcome screen
     * @param medicalConditions medical conditions
     * @return created user settings
     */
    fun createUserSettings(
            homeAddress: HomeAddress,
            pollutantPenalties: PollutantPenalties,
            pollutantThresholds: PollutantThresholds,
            showMobileWelcomeScreen: Boolean,
            medicalConditions: MedicalConditions
    ): UserSettings {
        val userSettings = UserSettings(showMobileWelcomeScreen, pollutantPenalties, pollutantThresholds, medicalConditions, homeAddress)
        return addClosable(api.createUserSettings(userSettings))
    }

    /**
     * Sends a request to update user settings
     *
     * @param homeAddress home address
     * @param pollutantPenalties pollutant penalties
     * @param pollutantThresholds pollutant thresholds
     * @param showMobileWelcomeScreen a boolean setting for showing the mobile welcome screen
     * @param medicalConditions medical conditions
     * @return updated user settings
     */
    fun updateUserSettings(
            homeAddress: HomeAddress,
            pollutantPenalties: PollutantPenalties,
            pollutantThresholds: PollutantThresholds,
            showMobileWelcomeScreen: Boolean,
            medicalConditions: MedicalConditions
    ): UserSettings {
        val userSettings = UserSettings(showMobileWelcomeScreen, pollutantPenalties, pollutantThresholds, medicalConditions, homeAddress)
        return api.updateUserSettings(userSettings)
    }

    /**
     * Sends a request user settings
     *
     * @return user settings
     */
    fun getUserSettings(): UserSettings {
        return api.getUserSettings()
    }

    /**
     * Downloads user data
     *
     * @return user data
     */
    fun downloadUserData(): File {
        val httpClient = HttpClient()
        val downloadRequest = GetMethod("${TestSettings.apiBasePath}/v1/users/data")
        downloadRequest.setRequestHeader("Authorization", "Bearer ${accessTokenProvider?.accessToken}")
        httpClient.executeMethod(downloadRequest)
        val data = downloadRequest.responseBody
        val zipFile = File("data.zip")
        val stream = FileOutputStream(zipFile)
        stream.write(data)
        stream.close()

        return zipFile
    }
}