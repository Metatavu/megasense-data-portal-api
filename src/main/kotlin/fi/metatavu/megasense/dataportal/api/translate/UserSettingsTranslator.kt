package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.api.spec.model.HomeAddress
import fi.metatavu.megasense.dataportal.api.spec.model.MedicalConditions
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantPenalties
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantThresholds
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
        val homeAddress: HomeAddress? = translateHomeAddress(entity)
        if (homeAddress != null) {
            userSettings.homeAddress = homeAddress
        }

        val medicalConditions: MedicalConditions = translateMedicalConditions(entity)

        userSettings.medicalConditions = medicalConditions
        userSettings.showMobileWelcomeScreen = entity.showMobileWelcomeScreen
        userSettings.pollutantPenalties = translatePollutantPenalties(entity)
        userSettings.pollutantThresholds = translatePollutantThresholds(entity)

        return userSettings
    }

    /**
     * Translates user settings entity into medical conditions
     *
     * @param entity user settings
     * @return medical conditions
     */
    private fun translateMedicalConditions(entity: UserSettings): MedicalConditions {
        val medicalConditions = MedicalConditions()
        medicalConditions.asthma = entity.asthma
        medicalConditions.ihd = entity.ihd
        medicalConditions.copd = entity.copd
        return medicalConditions
    }

    /**
     * Translates user settings entity into pollutant penalties
     *
     * @param entity user settings
     * @return pollutant penalties
     */
    private fun translatePollutantPenalties(entity: UserSettings): PollutantPenalties {
        val pollutantPenalties = PollutantPenalties()
        pollutantPenalties.carbonMonoxidePenalty = entity.carbonMonoxidePenalty
        pollutantPenalties.nitrogenMonoxidePenalty = entity.nitrogenMonoxidePenalty
        pollutantPenalties.nitrogenDioxidePenalty = entity.nitrogenDioxidePenalty
        pollutantPenalties.ozonePenalty = entity.ozonePenalty
        pollutantPenalties.sulfurDioxidePenalty = entity.sulfurDioxidePenalty
        pollutantPenalties.harmfulMicroparticlesPenalty = entity.harmfulMicroparticlesPenalty
        return pollutantPenalties
    }

    /**
     * Translates user settings entity into pollutant thresholds
     *
     * @param entity user settings
     * @return pollutant thresholds
     */
    private fun translatePollutantThresholds(entity: UserSettings): PollutantThresholds {
        val pollutantThresholds = PollutantThresholds()
        pollutantThresholds.carbonMonoxideThreshold = entity.carbonMonoxideThreshold
        pollutantThresholds.nitrogenMonoxideThreshold = entity.nitrogenMonoxideThreshold
        pollutantThresholds.nitrogenDioxideThreshold = entity.nitrogenDioxideThreshold
        pollutantThresholds.ozoneThreshold = entity.ozoneThreshold
        pollutantThresholds.sulfurDioxideThreshold = entity.sulfurDioxideThreshold
        pollutantThresholds.harmfulMicroparticlesThreshold = entity.harmfulMicroparticlesThreshold
        return pollutantThresholds;
    }

    /**
     * Translate home address from UserSettings entity
     *
     * @param entity
     * @return
     */
    private fun translateHomeAddress(entity: UserSettings): HomeAddress? {
        if (entity.city != null && entity.country != null && entity.postalCode != null && entity.streetAddress != null) {
            val homeAddress = HomeAddress()
            homeAddress.city = entity.city
            homeAddress.country = entity.country
            homeAddress.postalCode = entity.postalCode
            homeAddress.streetAddress = entity.streetAddress
            return homeAddress;
        }
        return null;
    }
}