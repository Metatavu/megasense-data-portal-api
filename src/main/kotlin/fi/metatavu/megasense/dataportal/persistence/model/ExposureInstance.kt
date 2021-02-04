package fi.metatavu.megasense.dataportal.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * JPA entity representing an exposure instance
 */
@Entity
class ExposureInstance {

    @Id
    var id: UUID? = null

    @ManyToOne
    var route: Route? = null

    @Column
    var startedAt: OffsetDateTime? = null

    @Column
    var endedAt: OffsetDateTime? = null

    @Column
    var carbonMonoxide: Float? = null

    @Column
    var nitrogenMonoxide: Float? = null

    @Column
    var nitrogenDioxide: Float? = null

    @Column
    var ozone: Float? = null

    @Column
    var sulfurDioxide: Float? = null

    @Column
    var harmfulMicroparticles: Float? = null

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