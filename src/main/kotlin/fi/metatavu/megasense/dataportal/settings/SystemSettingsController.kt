package fi.metatavu.megasense.dataportal.settings

import org.apache.commons.lang3.StringUtils
import javax.enterprise.context.ApplicationScoped

/**
 * System settings controller class used to read System settings
 */
@ApplicationScoped
class SystemSettingsController {

    /**
     * Returns environment variable
     *
     * @param key key
     * @return setting value
     */
    private fun getEnvPropertySetting(key: String): String? {
        return System.getenv(key)
    }

    /**
     * Returns Keycloak realm
     *
     * @return Keycloak realm
     */
    fun getKeycloakRealm(): String? {
        return getEnvPropertySetting("KEYCLOAK_REALM")
    }

    /**
     * Returns Keycloak URL
     *
     * @return Keycloak URL
     */
    fun getKeycloakUrl(): String? {
        return getEnvPropertySetting("KEYCLOAK_URL")
    }

    /**
     * Returns Keycloak admin user
     *
     * @return Keycloak admin user
     */
    fun getKeycloakAdminUser(): String? {
        return getEnvPropertySetting("KEYCLOAK_USER")
    }

    /**
     * Returns Keycloak admin pass
     *
     * @return Keycloak admin pass
     */
    fun getKeycloakAdminPassword(): String? {
        return getEnvPropertySetting("KEYCLOAK_PASSWORD")
    }

    /**
     * Returns Keycloak admin client id
     *
     * @return Keycloak admin client id
     */
    fun getKeycloakAdminClientId(): String? {
        return getEnvPropertySetting("KEYCLOAK_ADMIN_CLIENT_ID")
    }
}