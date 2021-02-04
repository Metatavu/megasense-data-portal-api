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
     * @param creatorId id of the user to whom these setting belong
     *
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
            creatorId: UUID
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

        userSettings.lastModifierId = creatorId

        return persist(userSettings)
    }

    /**
     * Updates pollutant penalties
     *
     * @param userSettings user settings to update
     * @param pollutantPenalties new pollutant penalties
     * @param lastModifierId id of the user who is modifying pollutant penalties
     *
     * @return updated user settings
     */
    fun updatePollutantPenalties (userSettings: UserSettings, pollutantPenalties: PollutantPenalties, lastModifierId: UUID): UserSettings {
        userSettings.carbonMonoxidePenalty = pollutantPenalties.carbonMonoxidePenalty
        userSettings.nitrogenMonoxidePenalty = pollutantPenalties.nitrogenMonoxidePenalty
        userSettings.nitrogenDioxidePenalty = pollutantPenalties.nitrogenDioxidePenalty
        userSettings.ozonePenalty = pollutantPenalties.ozonePenalty
        userSettings.sulfurDioxidePenalty = pollutantPenalties.sulfurDioxidePenalty
        userSettings.harmfulMicroparticlesPenalty = pollutantPenalties.harmfulMicroparticlesPenalty

        userSettings.lastModifierId = lastModifierId
        return persist(userSettings)
    }

    /**
     * Updates pollutant thresholds
     *
     * @param userSettings user settings to update
     * @param pollutantThresholds new pollutant thresholds
     * @param lastModifierId id of the user who is modifying pollutant thresholds
     *
     * @return updated user settings
     */
    fun updatePollutantThresholds (userSettings: UserSettings, pollutantThresholds: PollutantThresholds, lastModifierId: UUID): UserSettings {
        userSettings.carbonMonoxideThreshold = pollutantThresholds.carbonMonoxideThreshold
        userSettings.nitrogenMonoxideThreshold = pollutantThresholds.nitrogenMonoxideThreshold
        userSettings.nitrogenDioxideThreshold = pollutantThresholds.nitrogenDioxideThreshold
        userSettings.ozoneThreshold = pollutantThresholds.ozoneThreshold
        userSettings.sulfurDioxideThreshold = pollutantThresholds.sulfurDioxideThreshold
        userSettings.harmfulMicroparticlesThreshold = pollutantThresholds.harmfulMicroparticlesThreshold

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
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(UserSettings_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()));

        val query = entityManager.createQuery(criteria)

        if (query.resultList.size < 1) {
            return null
        }

        return query.resultList[0]
    }
}