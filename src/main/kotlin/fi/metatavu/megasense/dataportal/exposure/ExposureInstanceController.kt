package fi.metatavu.megasense.dataportal.exposure

import fi.metatavu.megasense.dataportal.persistence.dao.ExposureInstanceDAO
import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import fi.metatavu.megasense.dataportal.persistence.model.Route
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * A controller for creating exposure instances
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
    fun createExposureInstance (
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
        return exposureInstanceDAO.create(UUID.randomUUID(), route, startedAt, endedAt, carbonMonoxide, nitrogenMonoxide, nitrogenDioxide, ozone, sulfurDioxide, harmfulMicroparticles, creatorId)
    }

    /**
     * Lists exposure instances
     *
     * @param createdAfter list only instances created after this date
     * @param createdBefore list only instances created before this date
     *
     * @return exposure instances
     */
    fun listRoutes (createdAfter: OffsetDateTime?, createdBefore: OffsetDateTime?): List<ExposureInstance> {
        return exposureInstanceDAO.list(createdAfter, createdBefore)
    }

    /**
     * Finds an exposure instance
     *
     * @param exposureInstanceId id of the exposure instance to find
     *
     * @return found exposure instance or null if not found
     */
    fun findExposureInstance (exposureInstanceId: UUID): ExposureInstance? {
        return exposureInstanceDAO.findById(exposureInstanceId)
    }

    /**
     * Returns the total exposure
     */
    fun getTotalExposure (): ExposureInstance {
        val exposureInstances = exposureInstanceDAO.listAll()
        var totalHarmfulMicroparticles = 0f
        var totalSulfurDioxide = 0f
        var totalOzone = 0f
        var totalNitrogenDioxide = 0f
        var totalNitrogenMonoxide = 0f
        var totalCarbonMonoxide = 0f

        for (exposureInstance in exposureInstances) {
            val harmfulMicroparticles = exposureInstance.harmfulMicroparticles
            if (harmfulMicroparticles != null) {
                totalHarmfulMicroparticles += harmfulMicroparticles
            }

            val sulfurDioxide = exposureInstance.sulfurDioxide
            if (sulfurDioxide != null) {
                totalSulfurDioxide += sulfurDioxide
            }

            val ozone = exposureInstance.ozone
            if (ozone != null) {
                totalOzone += ozone
            }

            val nitrogenDioxide = exposureInstance.nitrogenDioxide
            if (nitrogenDioxide != null) {
                totalNitrogenDioxide += nitrogenDioxide
            }

            val nitrogenMonoxide = exposureInstance.nitrogenMonoxide
            if (nitrogenMonoxide != null) {
                totalNitrogenMonoxide += nitrogenMonoxide
            }

            val carbonMonoxide = exposureInstance.carbonMonoxide
            if (carbonMonoxide != null) {
                totalCarbonMonoxide += carbonMonoxide
            }
        }

        val exposureInstance = ExposureInstance()
        if (totalHarmfulMicroparticles > 0f) {
            exposureInstance.harmfulMicroparticles = totalHarmfulMicroparticles
        }

        if (totalSulfurDioxide > 0f) {
            exposureInstance.sulfurDioxide = totalSulfurDioxide
        }

        if (totalOzone > 0f) {
            exposureInstance.ozone = totalOzone
        }

        if (totalNitrogenDioxide > 0f) {
            exposureInstance.nitrogenDioxide = totalNitrogenDioxide
        }

        if (totalNitrogenMonoxide > 0f) {
            exposureInstance.nitrogenMonoxide = totalNitrogenMonoxide
        }

        if (totalCarbonMonoxide > 0f) {
            exposureInstance.carbonMonoxide = totalCarbonMonoxide
        }

        return exposureInstance
    }
}