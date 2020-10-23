package fi.metatavu.megasense.dataportal.settings

import org.apache.commons.lang3.StringUtils
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SystemSettingsController {
    /**
     * Returns system setting from any of setting sources.
     *
     * Settings are resolved in following order:
     *
     * 1) Environment variable
     * 2) System property
     * 3) Database
     * 4) Default value
     *
     * @param key key
     * @param defaultValue default value
     * @return setting value
     */
    private fun getEnvPropertySetting(key: String, defaultValue: String): String? {
        val result = System.getenv(key)
        return if (StringUtils.isNotBlank(result)) {
            result
        } else System.getProperty(key, defaultValue)
    }

    /**
     * Returns Keycloak realm
     *
     * @return Keycloak realm
     */
    fun getKeycloakRealm(): String? {
        return getEnvPropertySetting("KEYCLOAK_REALM", "")
    }

    /**
     * Returns Keycloak URL
     *
     * @return Keycloak URL
     */
    fun getKeycloakUrl(): String? {
        return getEnvPropertySetting("KEYCLOAK_URL", "")
    }

    /**
     * Returns Keycloak admin user
     *
     * @return Keycloak admin user
     */
    fun getKeycloakAdminUser(): String? {
        return getEnvPropertySetting("KEYCLOAK_USER", "")
    }

    /**
     * Returns Keycloak admin pass
     *
     * @return Keycloak admin pass
     */
    fun getKeycloakAdminPassword(): String? {
        return getEnvPropertySetting("KEYCLOAK_PASSWORD", "")
    }

    /**
     * Returns Keycloak admin client id
     *
     * @return Keycloak admin client id
     */
    fun getKeycloakAdminClientId(): String? {
        return getEnvPropertySetting("KEYCLOAK_ADMIN_CLIENT_ID", "")
    }
}