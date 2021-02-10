package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.ExposureInstancesApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.ExposureInstance
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings
import java.util.*

/**
 * Test builder resource for handling exposure instances
 */
class ExposureInstancesTestBuilderResource(testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<ExposureInstance, ApiClient> (testBuilder, apiClient) {

    /**
     * Sends a request to create an exposure instance
     *
     * @param routeId the route that gave this exposure
     * @param startedAt datetime at which this exposure began
     * @param endedAt datetime at which this exposure ended
     * @param carbonMonoxide the amount of carbon monoxide exposure
     * @param nitrogenMonoxide the amount of nitrogen monoxide exposure
     * @param nitrogenDioxide the amount of nitrogen dioxide exposure
     * @param ozone the amount of ozone exposure
     * @param sulfurDioxide the amount of sulfur dioxide exposure
     * @param harmfulMicroparticles the amount of harmful microparticles that user was exposed to
     *
     * @return created exposure instance
     */
    fun create(routeId: UUID?,
                startedAt: String?,
                endedAt: String?,
                carbonMonoxide: Float?,
                nitrogenMonoxide: Float?,
                nitrogenDioxide: Float?,
                ozone: Float?,
                sulfurDioxide: Float?,
                harmfulMicroparticles: Float?): ExposureInstance {
        val exposureInstance = ExposureInstance(
            id = null,
            routeId = routeId,
            startedAt = startedAt,
            endedAt = endedAt,
            carbonMonoxide = carbonMonoxide,
            nitrogenMonoxide = nitrogenMonoxide,
            nitrogenDioxide = nitrogenDioxide,
            ozone = ozone,
            sulfurDioxide = sulfurDioxide,
            harmfulMicroparticles = harmfulMicroparticles)
        return addClosable(api.createExposureInstance(exposureInstance))
    }

    /**
     * Sends a request to find an exposure instance
     *
     * @param exposureInstanceId the id of an exposure instance to find
     *
     * @return found exposure instance
     */
    fun find(exposureInstanceId: UUID): ExposureInstance {
        return api.findExposureInstance(exposureInstanceId)
    }

    /**
     * Sends a request to list exposure instances of an user
     *
     * @param createdBefore include only instances created before this date
     * @param createdAfter include only instances created after this date
     */
    fun list(createdBefore: String?, createdAfter: String?): Array<ExposureInstance> {
        return api.listExposureInstances(createdBefore, createdAfter)
    }

    override fun clean(exposureInstance: ExposureInstance) {
        api.deleteExposureInstance(exposureInstance.id!!)
    }

    override fun getApi(): ExposureInstancesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ExposureInstancesApi(TestSettings.apiBasePath)
    }
}