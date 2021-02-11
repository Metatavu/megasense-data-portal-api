package fi.metatavu.megasense.dataportal.exposure

import fi.metatavu.megasense.dataportal.persistence.dao.ExposureInstanceDAO
import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import fi.metatavu.megasense.dataportal.persistence.model.Route
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller for managing exposure instances
 */
@ApplicationScoped
class ExposureInstanceController {

    @Inject
    private lateinit var exposureInstanceDAO: ExposureInstanceDAO

    /**
     * Creates an exposure instance
     *
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
    fun createExposureInstance(
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
        return exposureInstanceDAO.create(UUID.randomUUID(), route, startedAt, endedAt, carbonMonoxide, nitrogenMonoxide, nitrogenDioxide, ozone, sulfurDioxide, harmfulMicroparticles, creatorId, creatorId)
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
    fun listExposureInstances(userId: UUID, createdBefore: OffsetDateTime?, createdAfter: OffsetDateTime?): List<ExposureInstance> {
        return exposureInstanceDAO.list(userId, createdBefore, createdAfter, null)
    }

    /**
     * Finds an exposure instance
     *
     * @param exposureInstanceId id of the exposure instance to find
     *
     * @return found exposure instance or null if not found
     */
    fun findExposureInstance(exposureInstanceId: UUID): ExposureInstance? {
        return exposureInstanceDAO.findById(exposureInstanceId)
    }

    /**
     * Returns the total exposure
     *
     * @param userId id of the user to whom the instances belong
     * @param exposedBefore include only instances created before this date
     * @param exposedAfter include only instances created after this date
     *
     * @return total exposure
     */
    fun getTotalExposure(userId: UUID, exposedBefore: OffsetDateTime?, exposedAfter: OffsetDateTime?): ExposureInstance {
        val exposureInstances = exposureInstanceDAO.list(userId, exposedBefore, exposedAfter, null)
        var totalHarmfulMicroparticles = 0f
        var totalSulfurDioxide = 0f
        var totalOzone = 0f
        var totalNitrogenDioxide = 0f
        var totalNitrogenMonoxide = 0f
        var totalCarbonMonoxide = 0f

        for (exposureInstance in exposureInstances) {
            totalHarmfulMicroparticles += exposureInstance.harmfulMicroparticles ?: 0f
            totalSulfurDioxide += exposureInstance.sulfurDioxide ?: 0f
            totalOzone += exposureInstance.ozone ?: 0f
            totalNitrogenDioxide += exposureInstance.nitrogenDioxide ?: 0f
            totalNitrogenMonoxide += exposureInstance.nitrogenMonoxide ?: 0f
            totalCarbonMonoxide += exposureInstance.carbonMonoxide ?: 0f
        }

        val exposureInstance = ExposureInstance()
        exposureInstance.harmfulMicroparticles = totalHarmfulMicroparticles
        exposureInstance.sulfurDioxide = totalSulfurDioxide
        exposureInstance.ozone = totalOzone
        exposureInstance.nitrogenDioxide = totalNitrogenDioxide
        exposureInstance.nitrogenMonoxide = totalNitrogenMonoxide
        exposureInstance.carbonMonoxide = totalCarbonMonoxide

        return exposureInstance
    }

    /**
     * Deletes an exposure instance
     *
     * @param exposureInstance exposure instance to delete
     */
    fun deleteExposureInstance(exposureInstance: ExposureInstance) {
        exposureInstanceDAO.clearRouteField(exposureInstance)
        exposureInstanceDAO.delete(exposureInstance)
    }
}