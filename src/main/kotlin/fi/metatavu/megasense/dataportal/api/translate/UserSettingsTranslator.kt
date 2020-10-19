package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import javax.enterprise.context.ApplicationScoped

/**
 * A translator class for user settings
 */
@ApplicationScoped
class UserSettingsTranslator: AbstractTranslator<fi.metatavu.megasense.dataportal.persistence.model.UserSettings, fi.metatavu.megasense.dataportal.api.spec.model.UserSettings>() {
    /**
     * Translates JPA user settings into REST user settings
     *
     * @param entity user settings to translate
     *
     * @return translated user settings
     */
    override fun translate(entity: UserSettings): fi.metatavu.megasense.dataportal.api.spec.model.UserSettings {
        val userSettings = fi.metatavu.megasense.dataportal.api.spec.model.UserSettings()
        userSettings.homeAddress = entity.homeAddress
        return userSettings
    }
}