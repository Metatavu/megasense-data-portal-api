package fi.metatavu.megasense.dataportal.api.translate

import fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance
import javax.enterprise.context.ApplicationScoped

/**
 * A translator class for exposure instances
 */
@ApplicationScoped
class ExposureInstanceTranslator: AbstractTranslator<fi.metatavu.megasense.dataportal.persistence.model.ExposureInstance, fi.metatavu.megasense.dataportal.api.spec.model.ExposureInstance>() {
    /**
     * Translates JPA exposure instances into REST exposure instances
     *
     * @param entity JPA exposure instance
     *
     * @return REST exposure instance
     */
    override fun translate(entity: ExposureInstance): fi.metatavu.megasense.dataportal.api.spec.model.ExposureInstance {
        val exposureInstance = fi.metatavu.megasense.dataportal.api.spec.model.ExposureInstance()
        exposureInstance.id = entity.id
        exposureInstance.routeId = entity.route?.id
        exposureInstance.startedAt = entity.startedAt
        exposureInstance.endedAt = entity.endedAt
        exposureInstance.carbonMonoxide = entity.carbonMonoxide?.toBigDecimal()
        exposureInstance.nitrogenMonoxide = entity.nitrogenMonoxide?.toBigDecimal()
        exposureInstance.nitrogenDioxide = entity.nitrogenDioxide?.toBigDecimal()
        exposureInstance.ozone = entity.ozone?.toBigDecimal()
        exposureInstance.sulfurDioxide = entity.sulfurDioxide?.toBigDecimal()
        exposureInstance.harmfulMicroparticles = entity.harmfulMicroparticles?.toBigDecimal()

        return exposureInstance
    }
}