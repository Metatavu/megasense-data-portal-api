package fi.metatavu.megasense.dataportal.persistence.dao

import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance_
import fi.metatavu.megasense.dataportal.persistence.model.Route
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate


/**
 * DAO class for exposure instance
 */
@ApplicationScoped
class ExposureInstanceDAO: AbstractDAO<ExposureInstance>() {
    /**
     * Creates an exposure instance
     *
     * @param id An UUID for identification
     * @param route the route that gave this exposure
     * @param startedAt datetime at which this exposure began
     * @param endedAt datetime at which this exposure ended
     * @param carbonMonoxide the amount of carbon monoxide exposure
     * @param nitrogenMonoxide the amount of nitrogen monoxide exposure
     * @param nitrogenDioxide the amount of nitrogen dioxide exposure
     * @param ozone the amount of ozone exposure
     * @param sulfurDioxide the amount of sulfur dioxide exposure
     * @param harmfulMicroparticles the amount of harmful microparticles that user was exposed to
     * @param creatorId id of the user who created this instance
     *
     * @return created exposure instance
     */
    fun create(
            id: UUID,
            route: Route?,
            startedAt: OffsetDateTime?,
            endedAt: OffsetDateTime?,
            carbonMonoxide: Float?,
            nitrogenMonoxide: Float?,
            nitrogenDioxide: Float?,
            ozone: Float?,
            sulfurDioxide: Float?,
            harmfulMicroparticles: Float?,
            creatorId: UUID): ExposureInstance {
        val exposureInstance = ExposureInstance()
        exposureInstance.id = id
        exposureInstance.route = route
        exposureInstance.startedAt = startedAt
        exposureInstance.endedAt = endedAt
        exposureInstance.carbonMonoxide = carbonMonoxide
        exposureInstance.nitrogenMonoxide = nitrogenMonoxide
        exposureInstance.nitrogenDioxide = nitrogenDioxide
        exposureInstance.ozone = ozone
        exposureInstance.sulfurDioxide = sulfurDioxide
        exposureInstance.harmfulMicroparticles = harmfulMicroparticles
        exposureInstance.creatorId = creatorId
        exposureInstance.lastModifierId = creatorId

        return persist(exposureInstance)
    }

    /**
     * Lists exposure instances
     *
     * @param userId id of the user to whom the instances belong
     * @param createdBefore list only instances created before this date
     * @param createdAfter list only instances created after this date
     *
     * @return exposure instances
     */
    fun list(userId: UUID, createdBefore: OffsetDateTime?, createdAfter: OffsetDateTime?): List<ExposureInstance> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(ExposureInstance::class.java)
        val root = criteria.from(ExposureInstance::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()
        if (createdAfter != null) {
            restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ExposureInstance_.createdAt), createdAfter))
        }

        if (createdBefore != null) {
            restrictions.add(criteriaBuilder.lessThanOrEqualTo(root.get(ExposureInstance_.createdAt), createdBefore))
        }

        restrictions.add(criteriaBuilder.equal(root.get(ExposureInstance_.creatorId), userId))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()));

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}