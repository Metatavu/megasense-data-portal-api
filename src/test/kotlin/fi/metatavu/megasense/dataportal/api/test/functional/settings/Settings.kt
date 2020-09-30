package fi.metatavu.megasense.dataportal.api.test.functional.settings

/**
 * Utility class for retrieving functional test settings
 *
 */
object TestSettings {

    /**
     * Returns API service base path
     */
    val apiBasePath: String
        get() = "http://localhost:1234/v1"

    /**
     * Returns API service base path
     */
    val filesBasePath: String
        get() = "http://localhost:1234/files"

    /**
     * Returns Keycloak host
     */
    val keycloakHost: String
        get() = "http://test-keycloak:8080/auth"

    /**
     * Returns Keycloak realm
     */
    val keycloakRealm: String
        get() = "naviwork"

    /**
     * Returns Keycloak client id
     */
    val keycloakClientId: String
        get() = "test"

    /**
     * Returns Keycloak client secret
     */
    val keycloakClientSecret: String?
        get() = null

    /**
     * Returns Keycloak admin user
     */
    val keycloakAdminUser: String
        get() = "admin"

    /**
     * Returns Keycloak admin password
     */
    val keycloakAdminPass: String
        get() = "test"

}