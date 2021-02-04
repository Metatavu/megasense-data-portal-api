package fi.metatavu.megasense.dataportal.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * JPA entity representing user settings
 */
@Entity
class Favourite {

    @Id
    var id: UUID? = null

    @Column
    var name: String? = null

    @Column
    var latitude: Float? = null

    @Column
    var longitude: Float? = null

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