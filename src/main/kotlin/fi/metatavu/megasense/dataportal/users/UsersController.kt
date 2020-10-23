package fi.metatavu.megasense.dataportal.users

import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import fi.metatavu.megasense.dataportal.route.RouteController
import fi.metatavu.megasense.dataportal.settings.SystemSettingsController
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.DeleteMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller for users
 */
@ApplicationScoped
class UsersController {
    @Inject
    private lateinit var userSettingsController: UserSettingsController

    @Inject
    private lateinit var routeController: RouteController

    @Inject
    private lateinit var exposureInstanceController: ExposureInstanceController

    @Inject
    private lateinit var systemSettingsController: SystemSettingsController

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

        userSettingsController.deleteUserSettings(userId)

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
}