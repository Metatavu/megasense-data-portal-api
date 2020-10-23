package fi.metatavu.megasense.dataportal.users

import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import fi.metatavu.megasense.dataportal.persistence.dao.UserSettingsDAO
import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import fi.metatavu.megasense.dataportal.persistence.model.Route
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import fi.metatavu.megasense.dataportal.route.RouteController
import fi.metatavu.megasense.dataportal.settings.SystemSettingsController
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.DeleteMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

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

        val keycloakUrl = systemSettingsController.getKeycloakUrl()
        val keycloakRealm = systemSettingsController.getKeycloakRealm()

        val keycloakUsername = systemSettingsController.getKeycloakAdminUser()
        val keycloakPassword = systemSettingsController.getKeycloakAdminPassword()
        val keycloakAdminClientId = systemSettingsController.getKeycloakAdminClientId()

        val client = HttpClient()

        val getTokenMethod = PostMethod("$keycloakUrl/realms/master/protocol/openid-connect/token")
        getTokenMethod.setParameter("username", keycloakUsername)
        getTokenMethod.setParameter("password", keycloakPassword)
        getTokenMethod.setParameter("grant_type", "password")
        getTokenMethod.setParameter("client_id", keycloakAdminClientId)
        client.executeMethod(getTokenMethod)
        val tokenJson = getTokenMethod.responseBodyAsString
        val tokenObject: JSONObject = JSONParser().parse(tokenJson) as JSONObject
        val token = tokenObject["access_token"]
        getTokenMethod.releaseConnection()

        val deleteMethod = DeleteMethod("$keycloakUrl/admin/realms/$keycloakRealm/users/$userId")
        deleteMethod.setRequestHeader("Authorization", "Bearer $token")
        client.executeMethod(deleteMethod)
        deleteMethod.releaseConnection()

        if (deleteMethod.statusCode < 200 || deleteMethod.statusCode > 299) {
            throw Error("Keycloak returned an error when deleting an user!")
        }
    }

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

    /**
     * Finds user data
     *
     * @param userId user id
     *
     * @return a zip file that contains user data
     */
    fun findUserData (userId: UUID): ByteArray {
        try {
            val exposureInstances = exposureInstanceController.listExposureInstances(userId, null, null)
            val routes = routeController.listRoutes(userId)

            val exposureFileWriter = FileWriter("exposure.csv")
            writeExposureInstancesToCsv(exposureFileWriter, exposureInstances)

            exposureFileWriter.flush()
            exposureFileWriter.close()

            val routesFileWriter = FileWriter("route.csv")
            writeRoutesToCsv(routesFileWriter, routes)

            routesFileWriter.flush()
            routesFileWriter.close()

            val zipOutputStream = ZipOutputStream(FileOutputStream("data.zip"))

            val exposureZipEntry = ZipEntry("exposure.csv")
            val routeZipEntry = ZipEntry("route.csv")
            zipOutputStream.putNextEntry(exposureZipEntry)
            zipOutputStream.closeEntry()
            zipOutputStream.putNextEntry(routeZipEntry)
            zipOutputStream.closeEntry()
            zipOutputStream.close()

            val bytes = File("data.zip").readBytes()

            File("data.zip").delete()
            File("route.csv").delete()
            File("exposure.csv").delete()

            return bytes
        } catch (exception: Exception) {
            File("data.zip").delete()
            File("route.csv").delete()
            File("exposure.csv").delete()

            throw exception
        }

    }

    /**
     * Writes exposure instances to a csv file
     *
     * @param fileWriter file writer
     * @param exposureInstances instances to write
     */
    private fun writeExposureInstancesToCsv (fileWriter: FileWriter, exposureInstances: List<ExposureInstance>) {
        fileWriter.append("Route, Started at, Ended at, Carbon monoxide, Nitrogen monoxide, Nitrogen dioxide, Ozone, Sulfur dioxide, Microparticles")
        fileWriter.append("\n")
        for (exposureInstance in exposureInstances) {
            fileWriter.append(exposureInstance.route?.id.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.startedAt.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.endedAt.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.carbonMonoxide.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.nitrogenMonoxide.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.nitrogenDioxide.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.ozone.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.sulfurDioxide.toString())
            fileWriter.append(",")

            fileWriter.append(exposureInstance.harmfulMicroparticles.toString())
            fileWriter.append("\n")
        }
    }

    /**
     * Writes routes to a csv file
     *
     * @param fileWriter file writer
     * @param routes routes to write
     */
    private fun writeRoutesToCsv (fileWriter: FileWriter, routes: List<Route>) {
        fileWriter.append("Id", "Route points", "Start location", "End location", "Saved at")
        fileWriter.append("\n")

        for (route in routes) {
            fileWriter.append(route.id.toString())
            fileWriter.append(",")

            fileWriter.append(route.routePoints)
            fileWriter.append(",")

            fileWriter.append(route.locationFromName)
            fileWriter.append(",")

            fileWriter.append(route.locationToName)
            fileWriter.append(",")

            fileWriter.append(route.createdAt.toString())
            fileWriter.append("\n")
        }
    }
}