package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.api.spec.model.PollutantPenalties
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantThresholds
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings_
import fi.metatavu.megasense.dataportal.persistence.model.UserSettings
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for user settings
 */
@ApplicationScoped
class UserSettingsDAO: AbstractDAO<UserSettings>() {

    /**
     * Creates user settings
     *
     * @param id UUID for identification
     * @param streetAddress street address
     * @param postalCode postal code
     * @param city city
     * @param country country
     * @param showMobileWelcomeScreen a boolean setting for showing the mobile welcome screen
     * @param pollutantPenalties pollutant penalties
     * @param pollutantThresholds pollutant thresholds
     * @param asthma asthma
     * @param ihd ihd
     * @param copd copd
     * @param creatorId id of the user to whom these setting belong
     * @param lastModifierId id of the user who was the last to modify the settings
     * @return created user settings
     */
    fun create (
            id: UUID,
            streetAddress: String?,
            postalCode: String?,
            city: String?,
            country: String?,
            showMobileWelcomeScreen: Boolean,
            pollutantPenalties: PollutantPenalties,
            pollutantThresholds: PollutantThresholds,
            asthma: Boolean,
            ihd: Boolean,
            copd: Boolean,
            creatorId: UUID,
            lastModifierId: UUID
    ): UserSettings {
        val userSettings = UserSettings()
        userSettings.id = id
        userSettings.streetAddress = streetAddress
        userSettings.postalCode = postalCode
        userSettings.city = city
        userSettings.country = country
        userSettings.showMobileWelcomeScreen = showMobileWelcomeScreen
        userSettings.creatorId = creatorId

        userSettings.carbonMonoxidePenalty = pollutantPenalties.carbonMonoxidePenalty
        userSettings.nitrogenMonoxidePenalty = pollutantPenalties.nitrogenMonoxidePenalty
        userSettings.nitrogenDioxidePenalty = pollutantPenalties.nitrogenDioxidePenalty
        userSettings.ozonePenalty = pollutantPenalties.ozonePenalty
        userSettings.sulfurDioxidePenalty = pollutantPenalties.sulfurDioxidePenalty
        userSettings.harmfulMicroparticlesPenalty = pollutantPenalties.harmfulMicroparticlesPenalty

        userSettings.carbonMonoxideThreshold = pollutantThresholds.carbonMonoxideThreshold
        userSettings.nitrogenMonoxideThreshold = pollutantThresholds.nitrogenMonoxideThreshold
        userSettings.nitrogenDioxideThreshold = pollutantThresholds.nitrogenDioxideThreshold
        userSettings.ozoneThreshold = pollutantThresholds.ozoneThreshold
        userSettings.sulfurDioxideThreshold = pollutantThresholds.sulfurDioxideThreshold
        userSettings.harmfulMicroparticlesThreshold = pollutantThresholds.harmfulMicroparticlesThreshold

        userSettings.asthma = asthma
        userSettings.ihd = ihd
        userSettings.copd = copd

        userSettings.lastModifierId = lastModifierId

        return persist(userSettings)
    }

    /**
     * Finds user settings belonging to a specific user
     *
     * @param userId user id
     *
     * @return user settings
     */
    fun findByUserId (userId: UUID): UserSettings? {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(UserSettings::class.java)
        val root = criteria.from(UserSettings::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(UserSettings_.creatorId), userId))
        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Update carbon monoxide penalty
     *
     * @param userSettings user settings to update
     * @param carbonMonoxidePenalty new value of carbonMonoxidePenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateCarbonMonoxidePenalty(userSettings: UserSettings, carbonMonoxidePenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.carbonMonoxidePenalty = carbonMonoxidePenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update nitrogen monoxide penalty
     *
     * @param userSettings user settings to update
     * @param nitrogenMonoxidePenalty new value of nitrogenMonoxidePenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateNitrogenMonoxidePenalty(userSettings: UserSettings, nitrogenMonoxidePenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.nitrogenMonoxidePenalty = nitrogenMonoxidePenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update nitrogen dioxide penalty
     *
     * @param userSettings user settings to update
     * @param nitrogenDioxidePenalty new value of nitrogenDioxidePenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateNitrogenDioxidePenalty(userSettings: UserSettings, nitrogenDioxidePenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.nitrogenDioxidePenalty = nitrogenDioxidePenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update ozone penalty
     *
     * @param userSettings user settings to update
     * @param ozonePenalty new value of ozonePenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateOzonePenalty(userSettings: UserSettings, ozonePenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.ozonePenalty = ozonePenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update sulfur dioxide penalty
     *
     * @param userSettings user settings to update
     * @param sulfurDioxidePenalty new value of sulfurDioxidePenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateSulfurDioxidePenalty(userSettings: UserSettings, sulfurDioxidePenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.sulfurDioxidePenalty = sulfurDioxidePenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update harmful microparticles penalty
     *
     * @param userSettings user settings to update
     * @param harmfulMicroparticlesPenalty new value of harmfulMicroparticlesPenalty
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateHarmfulMicroparticlesPenalty(userSettings: UserSettings, harmfulMicroparticlesPenalty: Float, lastModifierId: UUID): UserSettings {
        userSettings.harmfulMicroparticlesPenalty = harmfulMicroparticlesPenalty
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update carbon monoxide threshold
     *
     * @param userSettings user settings to update
     * @param carbonMonoxideThreshold new value of carbonMonoxideThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateCarbonMonoxideThreshold(userSettings: UserSettings, carbonMonoxideThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.carbonMonoxideThreshold = carbonMonoxideThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update nitrogen monoxide threshold
     *
     * @param userSettings user settings to update
     * @param nitrogenMonoxideThreshold new value of nitrogenMonoxideThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateNitrogenMonoxideThreshold(userSettings: UserSettings, nitrogenMonoxideThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.nitrogenMonoxideThreshold = nitrogenMonoxideThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update nitrogen dioxide threshold
     *
     * @param userSettings user settings to update
     * @param nitrogenDioxideThreshold new value of nitrogenDioxideThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateNitrogenDioxideThreshold(userSettings: UserSettings, nitrogenDioxideThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.nitrogenDioxideThreshold = nitrogenDioxideThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update ozone threshold
     *
     * @param userSettings user settings to update
     * @param ozoneThreshold new value of ozoneThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateOzoneThreshold(userSettings: UserSettings, ozoneThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.ozoneThreshold = ozoneThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Update sulfur dioxide threshold
     *
     * @param userSettings user settings to update
     * @param sulfurDioxideThreshold new value of sulfurDioxideThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateSulfurDioxideThreshold(userSettings: UserSettings, sulfurDioxideThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.sulfurDioxideThreshold = sulfurDioxideThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Updates harmful microparticles threshold
     *
     * @param userSettings user settings to update
     * @param harmfulMicroparticlesThreshold new value of harmfulMicroparticlesThreshold
     * @param lastModifierId id of the user who is modifying this setting
     * @return updated user settings
     */
    fun updateHarmfulMicroparticlesThreshold(userSettings: UserSettings, harmfulMicroparticlesThreshold: Float, lastModifierId: UUID): UserSettings {
        userSettings.harmfulMicroparticlesThreshold = harmfulMicroparticlesThreshold
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Updates the setting for showing mobile welcome screen
     *
     * @param userSettings user settings to update
     * @param showMobileWelcomeScreen a new value for this setting
     * @param lastModifierId id of the user who is modifying this setting
     *
     * @return updated user settings
     */
    fun updateShowMobileWelcomeScreen (userSettings: UserSettings, showMobileWelcomeScreen: Boolean, lastModifierId: UUID): UserSettings {
        userSettings.showMobileWelcomeScreen = showMobileWelcomeScreen
        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Updates the street address of an user
     *
     * @param userSettings user settings to update
     * @param streetAddress new street address
     * @param modifierId id of the user who is modifying this setting
     *
     * @return updated user settings
     */
    fun updateStreetAddress (userSettings: UserSettings, streetAddress: String?, modifierId: UUID): UserSettings {
        userSettings.streetAddress = streetAddress
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the postal code of an user
     *
     * @param userSettings user settings to update
     * @param postalCode new postal code
     * @param modifierId id of the user who is modifying this setting
     *
     * @return updated user settings
     */
    fun updatePostalCode (userSettings: UserSettings, postalCode: String?, modifierId: UUID): UserSettings {
        userSettings.postalCode = postalCode
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the city of an user
     *
     * @param userSettings user settings to update
     * @param city new city
     * @param modifierId id of the user who is modifying this setting
     *
     * @return updated user settings
     */
    fun updateCity (userSettings: UserSettings, city: String?, modifierId: UUID): UserSettings {
        userSettings.city = city
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates the country of an user
     *
     * @param userSettings user settings to update
     * @param country country
     * @param modifierId id of the user who is modifying this setting
     *
     * @return updated user settings
     */
    fun updateCountry (userSettings: UserSettings, country: String?, modifierId: UUID): UserSettings {
        userSettings.country = country
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates asthma status
     *
     * @param userSettings user settings to update
     * @param asthma asthma
     * @param modifierId id of user who changes the property
     * @return updated user settings
     */
    fun updateAsthma(userSettings: UserSettings, asthma: Boolean, modifierId: UUID): UserSettings {
        userSettings.asthma = asthma
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates ihd status
     *
     * @param userSettings user settings to update
     * @param ihd Ischemic Heart Disease
     * @param modifierId id of user who changes the property
     * @return updated user settings
     */
    fun updateIhd(userSettings: UserSettings, ihd: Boolean, modifierId: UUID): UserSettings {
        userSettings.ihd = ihd
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }

    /**
     * Updates copd status
     *
     * @param userSettings user settings to update
     * @param copd Chronic obstructive pulmonary disease
     * @param modifierId id of user who changes the property
     * @return updated user settings
     */
    fun updateCopd(userSettings: UserSettings, copd: Boolean, modifierId: UUID): UserSettings {
        userSettings.copd = copd
        userSettings.lastModifierId = modifierId
        return persist(userSettings)
    }
}