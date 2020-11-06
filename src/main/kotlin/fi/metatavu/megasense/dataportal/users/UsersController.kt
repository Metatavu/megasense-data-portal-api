package fi.metatavu.megasense.dataportal.users

import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import fi.metatavu.megasense.dataportal.persistence.dao.UserSettingsDAO
import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import fi.metatavu.megasense.dataportal.persistence.model.Route
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import fi.metatavu.megasense.dataportal.route.RouteController
import fi.metatavu.megasense.dataportal.settings.SystemSettingsController
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import java.io.File
import java.io.FileOutputStream

import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller for users
 */
@ApplicationScoped
class UsersController {

    @Inject
    private lateinit var routeController: RouteController

    @Inject
    private lateinit var exposureInstanceController: ExposureInstanceController

    @Inject
    private lateinit var systemSettingsController: SystemSettingsController

    @Inject
    private lateinit var userSettingsDAO: UserSettingsDAO

    /**
     * Deletes an user
     *
     * @param userId id of the user to delete
     */
    fun deleteUser(userId: UUID) {
        val userRoutes = routeController.listRoutes(userId)

        for (route in userRoutes) {
            routeController.deleteRoute(route, userId)
        }

        val userExposureInstances = exposureInstanceController.listExposureInstances(userId, null, null)

        for (instance in userExposureInstances) {
            exposureInstanceController.deleteExposureInstance(instance)
        }

        deleteUserSettings(userId)

        val keycloak = getKeycloakClient()
        val usersResource = keycloak.realm(systemSettingsController.getKeycloakRealm()).users()
        val deleteResponse = usersResource.delete(userId.toString())

        if (deleteResponse.status < 200 || deleteResponse.status > 299) {
            throw Error("Keycloak returned an error when deleting an user!")
        }
    }

    /**
     * Returns an authenticated Keycloak client
     *
     * @return authenticated Keycloak client
     */
    private fun getKeycloakClient(): Keycloak {
        return KeycloakBuilder
                .builder()
                .grantType(null)
                .username(systemSettingsController.getKeycloakAdminUser())
                .password(systemSettingsController.getKeycloakAdminPassword())
                .realm("master")
                .clientId(systemSettingsController.getKeycloakAdminClientId())
                .serverUrl(systemSettingsController.getKeycloakUrl())
                .build()
    }

    /**
     * Creates user settings
     *
     * @param streetAddress street address
     * @param postalCode postal code
     * @param city city
     * @param country country
     * @param showMobileWelcomeScreen a boolean setting for showing the mobile welcome screen
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun createUserSettings (streetAddress: String?, postalCode: String?, city: String?, country: String?, showMobileWelcomeScreen: Boolean, creatorId: UUID): UserSettings {
        return userSettingsDAO.create(UUID.randomUUID(), streetAddress, postalCode, city, country, showMobileWelcomeScreen, creatorId)
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
     * @param showMobileWelcomeScreen a boolean setting for showing the mobile welcome screen
     * @param modifierId id of the user who is modifying these settings
     *
     * @return updated user settings
     */
    fun updateUserSettings (userSettings: UserSettings, streetAddress: String?, postalCode: String?, city: String?, country: String?, showMobileWelcomeScreen: Boolean, modifierId: UUID): UserSettings {
        userSettingsDAO.updateStreetAddress(userSettings, streetAddress, modifierId)
        userSettingsDAO.updatePostalCode(userSettings, postalCode, modifierId)
        userSettingsDAO.updateCity(userSettings, city, modifierId)
        userSettingsDAO.updateCountry(userSettings, country, modifierId)
        userSettingsDAO.updateShowMobileWelcomeScreen(userSettings, showMobileWelcomeScreen, modifierId)

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

    /**
     * Finds user data
     *
     * @param userId user id
     *
     * @return a zip file that contains user data
     */
    fun findUserData (userId: UUID): ByteArray {
        val time = System.currentTimeMillis()
        try {
            val exposureInstances = exposureInstanceController.listExposureInstances(userId, null, null)
            val routes = routeController.listRoutes(userId)
            val exposureBytes = writeExposureInstancesToCsv(exposureInstances).toByteArray()
            val routeBytes = writeRoutesToCsv(routes).toByteArray()

            val zipOutputStream = ZipOutputStream(FileOutputStream("data-$time.zip"))
            val exposureZipEntry = ZipEntry("exposure.csv")
            val routeZipEntry = ZipEntry("routes.csv")

            zipOutputStream.putNextEntry(exposureZipEntry)
            zipOutputStream.write(exposureBytes)
            zipOutputStream.closeEntry()
            zipOutputStream.putNextEntry(routeZipEntry)
            zipOutputStream.write(routeBytes)
            zipOutputStream.closeEntry()
            zipOutputStream.close()

            val bytes = File("data-$time.zip").readBytes()
            File("data-$time.zip").delete()

            return bytes
        } catch (exception: Exception) {
            File("data-$time.zip").delete()
            throw exception
        }

    }

    /**
     * Writes exposure instances to a csv string
     *
     * @param exposureInstances instances to write
     *
     * @return csv strings
     */
    private fun writeExposureInstancesToCsv (exposureInstances: List<ExposureInstance>): String {
        var csv = "Route,Started at,Ended at,Carbon monoxide,Nitrogen monoxide,Nitrogen dioxide,Ozone,Sulfur dioxide,Microparticles"
        csv += "\n"
        for (exposureInstance in exposureInstances) {
            csv += anyToString(exposureInstance.route?.id)
            csv += ","

            csv += anyToString(exposureInstance.startedAt)
            csv += ","

            csv += anyToString(exposureInstance.endedAt)
            csv += ","

            csv += anyToString(exposureInstance.carbonMonoxide)
            csv += ","

            csv += anyToString(exposureInstance.nitrogenMonoxide)
            csv += ","

            csv += anyToString(exposureInstance.nitrogenDioxide)
            csv += ","

            csv += anyToString(exposureInstance.ozone)
            csv += ","

            csv += anyToString(exposureInstance.sulfurDioxide)
            csv += ","

            csv += anyToString(exposureInstance.harmfulMicroparticles)
            csv += "\n"
        }

        return csv
    }

    /**
     * Writes routes to a csv string
     *
     * @param routes routes to write
     *
     * @return csv string
     */
    private fun writeRoutesToCsv (routes: List<Route>): String {
        var csv = "Id,Route points,Start location,End location,Saved at"
        csv += "\n"

        for (route in routes) {
            csv += anyToString(route.id)
            csv += ","

            csv += anyToString(route.routePoints)
            csv += ","

            csv += anyToString(route.locationFromName)
            csv += ","

            csv += anyToString(route.locationToName)
            csv += ","

            csv += anyToString(route.createdAt)
            csv += "\n"
        }

        return csv
    }

    /**
     * Returns string value of a parameter or empty string if the parameter is null
     *
     * @param parameter
     *
     * @return string value
     */
    private fun anyToString (parameter: Any?): String {
        if (parameter == null) {
            return ""
        }

        return parameter.toString()
    }
}