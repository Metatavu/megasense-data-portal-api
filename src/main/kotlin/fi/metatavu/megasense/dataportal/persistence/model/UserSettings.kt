package fi.metatavu.megasense.dataportal.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * JPA entity representing user settings
 */
@Entity
class UserSettings {

    @Id
    var id: UUID? = null

    /** Home address */
    @Column
    var streetAddress: String? = null

    @Column
    var postalCode: String? = null

    @Column
    var city: String? = null

    @Column
    var country: String? = null

    /** Show mobile screen */

    @Column(nullable = false)
    var showMobileWelcomeScreen: Boolean? = null

    /** Pollutant thresholds */

    @Column
    var carbonMonoxideThreshold: Float? = null

    @Column
    var nitrogenMonoxideThreshold: Float? = null

    @Column
    var nitrogenDioxideThreshold: Float? = null

    @Column
    var ozoneThreshold: Float? = null

    @Column
    var sulfurDioxideThreshold: Float? = null

    @Column
    var harmfulMicroparticlesThreshold: Float? = null

    /** Pollutant penalties */

    @Column
    var carbonMonoxidePenalty: Float? = null

    @Column
    var nitrogenMonoxidePenalty: Float? = null

    @Column
    var nitrogenDioxidePenalty: Float? = null

    @Column
    var ozonePenalty: Float? = null

    @Column
    var sulfurDioxidePenalty: Float? = null

    @Column
    var harmfulMicroparticlesPenalty: Float? = null

    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    @Column(nullable = false)
    var creatorId: UUID? = null

    @Column(nullable = false)
    var lastModifierId: UUID? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now()
        modifiedAt = OffsetDateTime.now()
    }

    /**
     * JPA pre-update event handler
     */
    @PreUpdate
    fun onUpdate() {
        modifiedAt = OffsetDateTime.now()
    }
}