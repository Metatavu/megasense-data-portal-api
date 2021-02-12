package fi.metatavu.megasense.dataportal.settings

import org.eclipse.microprofile.config.ConfigProvider
import javax.enterprise.context.ApplicationScoped

/**
 * System settings controller class used to config properties
 */
@ApplicationScoped
class SystemSettingsController {

    /**
     * Returns keycloak property by name
     *
     * @param key key
     * @return setting value
     */
    private fun getKeycloakConfigProperty(key: String): String? {
        return ConfigProvider.getConfig().getValue("megasense.keycloak.$key", String::class.java)
    }

    /**
     * Returns Keycloak realm
     *
     * @return Keycloak realm
     */
    fun getKeycloakRealm(): String? {
        return getKeycloakConfigProperty("realm")
    }

    /**
     * Returns Keycloak URL
     *
     * @return Keycloak URL
     */
    fun getKeycloakUrl(): String? {
        return getKeycloakConfigProperty("host")
    }

    /**
     * Returns Keycloak admin user
     *
     * @return Keycloak admin user
     */
    fun getKeycloakAdminUser(): String? {
        return getKeycloakConfigProperty("user")
    }

    /**
     * Returns Keycloak admin pass
     *
     * @return Keycloak admin pass
     */
    fun getKeycloakAdminPassword(): String? {
        return getKeycloakConfigProperty("password")
    }

    /**
     * Returns Keycloak admin client id
     *
     * @return Keycloak admin client id
     */
    fun getKeycloakAdminClientId(): String? {
        return getKeycloakConfigProperty("admin_client_id")
    }
}