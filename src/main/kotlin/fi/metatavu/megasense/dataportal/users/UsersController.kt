package fi.metatavu.megasense.dataportal.users

import fi.metatavu.megasense.dataportal.api.spec.model.PollutantPenalties
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantThresholds
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
import java.lang.IllegalStateException

import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.eclipse.microprofile.config.ConfigProvider
import java.io.StringWriter

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

        val userExposureInstances = exposureInstanceController.listExposureInstances(
            userId = userId,
            createdBefore = null,
            createdAfter = null
        )

        for (instance in userExposureInstances) {
            exposureInstanceController.deleteExposureInstance(instance)
        }

        deleteUserSettings(userId)

        val keycloak = getKeycloakClient()
        val usersResource = keycloak.realm(systemSettingsController.getKeycloakRealm()).users()
        val deleteResponse = usersResource.delete(userId.toString())

        if (deleteResponse.status < 200 || deleteResponse.status > 299) {
            throw IllegalStateException("Keycloak returned an error when deleting an user!")
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
                .realm(ConfigProvider.getConfig().getValue("keycloak.realm", String::class.java))
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
     * @param pollutantPenalties pollutant penalties
     * @param pollutantThresholds pollutant thresholds
     * @param creatorId id of the user to whom these setting belong
     *
     * @return created user settings
     */
    fun createUserSettings (
            streetAddress: String?,
            postalCode: String?,
            city: String?,
            country: String?,
            showMobileWelcomeScreen: Boolean,
            pollutantPenalties: PollutantPenalties,
            pollutantThresholds: PollutantThresholds,
            creatorId: UUID): UserSettings {
        return userSettingsDAO.create(
            id = UUID.randomUUID(),
            streetAddress = streetAddress,
            postalCode = postalCode,
            city = city,
            country = country,
            showMobileWelcomeScreen = showMobileWelcomeScreen,
            pollutantPenalties = pollutantPenalties,
            pollutantThresholds = pollutantThresholds,
            creatorId = creatorId,
            lastModifierId = creatorId
        )
    }

    /**
     * Finds user settings belonging to a specific user
     *
     * @param userId user id
     *
     * @return user settings
     */
    fun findUserSettings(userId: UUID): UserSettings? {
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
     * @param pollutantPenalties pollutant penalties
     * @param pollutantThresholds pollutant thresholds
     * @param modifierId id of the user who is modifying these settings
     *
     * @return updated user settings
     */
    fun updateUserSettings (
            userSettings: UserSettings,
            streetAddress: String?,
            postalCode: String?,
            city: String?,
            country: String?,
            showMobileWelcomeScreen: Boolean,
            pollutantPenalties: PollutantPenalties,
            pollutantThresholds: PollutantThresholds,
            modifierId: UUID): UserSettings {
        var result = userSettingsDAO.updateStreetAddress(userSettings, streetAddress, modifierId)
        result = userSettingsDAO.updatePostalCode(result, postalCode, modifierId)
        result = userSettingsDAO.updateCity(result, city, modifierId)
        result = userSettingsDAO.updateCountry(result, country, modifierId)
        result = userSettingsDAO.updateShowMobileWelcomeScreen(result, showMobileWelcomeScreen, modifierId)
        result = updatePollutantPenalties(result, pollutantPenalties, modifierId)
        result = updatePollutantThresholds(result, pollutantThresholds, modifierId)
        return result
    }

    /**
     * Updates pollutant penalties
     *
     * @param userSettings user settings to update
     * @param pollutantPenalties new pollutant penalties
     * @param lastModifierId id of the user who is modifying pollutant penalties
     *
     * @return updated user settings
     */
    private fun updatePollutantPenalties(userSettings: UserSettings, pollutantPenalties: PollutantPenalties, modifierId: UUID): UserSettings {
        var result = userSettingsDAO.updateCarbonMonoxidePenalty(userSettings, pollutantPenalties.carbonMonoxidePenalty, modifierId)
        result = userSettingsDAO.updateNitrogenMonoxidePenalty(result, pollutantPenalties.nitrogenMonoxidePenalty, modifierId)
        result = userSettingsDAO.updateNitrogenDioxidePenalty(result, pollutantPenalties.nitrogenDioxidePenalty, modifierId)
        result = userSettingsDAO.updateOzonePenalty(result, pollutantPenalties.ozonePenalty, modifierId)
        result = userSettingsDAO.updateSulfurDioxidePenalty(result, pollutantPenalties.sulfurDioxidePenalty, modifierId)
        result = userSettingsDAO.updateHarmfulMicroparticlesPenalty(result, pollutantPenalties.harmfulMicroparticlesPenalty, modifierId)
        return result
    }

    /**
     * Updates pollutant thresholds
     *
     * @param userSettings user settings to update
     * @param pollutantThresholds new pollutant thresholds
     * @param lastModifierId id of the user who is modifying pollutant thresholds
     *
     * @return updated user settings
     */
    private fun updatePollutantThresholds(userSettings: UserSettings, pollutantThresholds: PollutantThresholds, modifierId: UUID): UserSettings {
        var result = userSettingsDAO.updateCarbonMonoxideThreshold(userSettings, pollutantThresholds.carbonMonoxideThreshold, modifierId)
        result = userSettingsDAO.updateNitrogenMonoxideThreshold(result, pollutantThresholds.nitrogenMonoxideThreshold, modifierId)
        result = userSettingsDAO.updateNitrogenDioxideThreshold(result, pollutantThresholds.nitrogenDioxideThreshold, modifierId)
        result = userSettingsDAO.updateOzoneThreshold(result, pollutantThresholds.ozoneThreshold, modifierId)
        result = userSettingsDAO.updateSulfurDioxideThreshold(result, pollutantThresholds.sulfurDioxideThreshold, modifierId)
        result = userSettingsDAO.updateHarmfulMicroparticlesThreshold(result, pollutantThresholds.harmfulMicroparticlesThreshold, modifierId)
        return result
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
        val writer =  StringWriter()

        CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Route", "Started at", "Ended at", "Carbon monoxide", "Nitrogen monoxide", "Nitrogen dioxide",
            "Ozone", "Sulfur dioxide", "Microparticles"))
            .use { csvPrinter ->
                for (exposureInstance in exposureInstances)
                    csvPrinter.printRecord(exposureInstance.route?.id,
                        exposureInstance.startedAt,
                        exposureInstance.endedAt,
                        exposureInstance.carbonMonoxide,
                        exposureInstance.nitrogenMonoxide,
                        exposureInstance.nitrogenDioxide,
                        exposureInstance.ozone,
                        exposureInstance.sulfurDioxide,
                        exposureInstance.harmfulMicroparticles)
            }
        val result: String = writer.toString()
        writer.flush()
        writer.close()

        return result
    }

    /**
     * Writes routes to a csv string
     *
     * @param routes routes to write
     *
     * @return csv string
     */
    private fun writeRoutesToCsv (routes: List<Route>): String {
        val writer =  StringWriter()
        CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Id", "Route points", "Start location", "End location", "Saved at"))
            .use { csvPrinter ->
                for (route in routes)
                    csvPrinter.printRecord(route.id,
                        route.routePoints,
                        route.locationFromName,
                        route.locationToName,
                        route.createdAt)
            }
        val result: String = writer.toString()
        writer.flush()
        writer.close()

        return result
    }
}