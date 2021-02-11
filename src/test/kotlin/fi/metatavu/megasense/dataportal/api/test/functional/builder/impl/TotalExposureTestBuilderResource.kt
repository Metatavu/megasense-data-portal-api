package fi.metatavu.megasense.dataportal.api.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.megasense.dataportal.api.client.apis.TotalExposureApi
import fi.metatavu.megasense.dataportal.api.client.infrastructure.ApiClient
import fi.metatavu.megasense.dataportal.api.client.models.ExposureInstance
import fi.metatavu.megasense.dataportal.api.test.functional.settings.TestSettings

/**
 * Test builder resource for handling total exposure
 */
class TotalExposureTestBuilderResource (testBuilder: AbstractTestBuilder<ApiClient?>?, private val accessTokenProvider: AccessTokenProvider?, apiClient: ApiClient): ApiTestBuilderResource<ExposureInstance, ApiClient> (testBuilder, apiClient) {

    /**
     * Sends a request to get the total exposure of an user
     *
     * @param exposedBefore include only instances created before this date
     * @param exposedAfter include only instances created after this date
     *
     * @return total exposure
     */
    fun getTotalExposure (exposedBefore: String?, exposedAfter: String?): ExposureInstance {
        return api.totalExposure(exposedBefore, exposedAfter)
    }

    override fun clean (exposureInstance: ExposureInstance?) {
        TODO("Not yet implemented")
    }

    override fun getApi(): TotalExposureApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return TotalExposureApi(TestSettings.apiBasePath)
    }
}