package fi.metatavu.megasense.dataportal.api.test.functional.resources

import dasniko.testcontainers.keycloak.KeycloakContainer
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

/**
 * Quarkus test resource for providing Keycloak services
 */
class KeycloakResource : QuarkusTestResourceLifecycleManager {
    override fun start(): Map<String, String> {
        keycloak.start()

        val config: MutableMap<String, String> = HashMap()
        config["quarkus.oidc.auth-server-url"] = String.format("%s/realms/megasense-data-portal", keycloak.authServerUrl)
        config["quarkus.oidc.client-id"] = "api"
        config["megasense.keycloak.host"] = keycloak.authServerUrl
        config["KEYCLOAK_REALM"] = "megasense-data-portal"
        config["KEYCLOAK_USER"] = "admin"
        config["KEYCLOAK_PASSWORD"] = "test"
        config["KEYCLOAK_ADMIN_CLIENT_ID"] = "test"

        return config
    }

    override fun stop() {
        keycloak.stop()
    }

    companion object {
        var keycloak = KeycloakContainer()
            .withRealmImportFile("kc.json")
    }
}