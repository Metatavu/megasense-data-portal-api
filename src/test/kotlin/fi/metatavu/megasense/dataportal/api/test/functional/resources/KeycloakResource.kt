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
        config["megasense.keycloak.realm"] = "megasense-data-portal"
        config["megasense.keycloak.user"] = "admin"
        config["megasense.keycloak.password"] = "test"
        config["megasense.keycloak.admin_client_id"] = "test"

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