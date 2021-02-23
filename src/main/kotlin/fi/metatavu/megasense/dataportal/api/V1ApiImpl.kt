package fi.metatavu.megasense.dataportal.api

import fi.metatavu.megasense.dataportal.airquality.AirQualityController
import fi.metatavu.megasense.dataportal.api.spec.V1Api
import fi.metatavu.megasense.dataportal.api.spec.model.ExposureInstance
import fi.metatavu.megasense.dataportal.api.spec.model.FavouriteLocation
import fi.metatavu.megasense.dataportal.api.spec.model.UserSettings
import fi.metatavu.megasense.dataportal.api.translate.ExposureInstanceTranslator
import fi.metatavu.megasense.dataportal.api.translate.FavouritesTranslator
import fi.metatavu.megasense.dataportal.api.translate.RouteTranslator
import fi.metatavu.megasense.dataportal.api.translate.UserSettingsTranslator
import fi.metatavu.megasense.dataportal.exposure.ExposureInstanceController
import fi.metatavu.megasense.dataportal.favourites.FavouritesController
import fi.metatavu.megasense.dataportal.persistence.model.Route
import fi.metatavu.megasense.dataportal.route.RouteController
import fi.metatavu.megasense.dataportal.users.UsersController
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

/**
 * Endpoints for air quality
 */
@RequestScoped
@Transactional
class V1ApiImpl: V1Api, AbstractApi() {

    @Inject
    private lateinit var airQualityController: AirQualityController

    @Inject
    private lateinit var usersController: UsersController

    @Inject
    private lateinit var userSettingsTranslator: UserSettingsTranslator

    @Inject
    private lateinit var exposureInstanceTranslator: ExposureInstanceTranslator

    @Inject
    private lateinit var exposureInstanceController: ExposureInstanceController

    @Inject
    private lateinit var routeController: RouteController

    @Inject
    private lateinit var routeTranslator: RouteTranslator

    @Inject
    private lateinit var favouritesController: FavouritesController

    @Inject
    private lateinit var favouritesTranslator: FavouritesTranslator

    /* AirQuality */

    override fun getAirQuality(pollutant: String, boundingBoxCorner1: String, boundingBoxCorner2: String): Response {
        return createOk(airQualityController.getAirQuality(pollutant, boundingBoxCorner1, boundingBoxCorner2))
    }

    override fun getAirQualityForCoordinates(coordinates: String, pollutant: String): Response {
        return createOk(airQualityController.getAirQualityForCoordinates(pollutant, coordinates))
    }

    override fun getRouteAirQuality(coordinates: MutableList<String>): Response {
        return createOk(airQualityController.getAirQualityList(coordinates))
    }

    /* Users */

    override fun createUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId)
        if (foundUserSettings != null) {
            return createBadRequest("User settings already exist. Use a PUT-request to update user settings.")
        }

        val homeAddress = userSettings.homeAddress
        val createdUserSettings = usersController.createUserSettings(
            homeAddress?.streetAddress,
            homeAddress?.postalCode,
            homeAddress?.city,
            homeAddress?.country,
            userSettings.showMobileWelcomeScreen,
            userSettings.pollutantPenalties,
            userSettings.pollutantThresholds,
            userId
        )
        return createOk(userSettingsTranslator.translate(createdUserSettings))
    }

    override fun getUserSettings(): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound("User settings not found!")
        return createOk(userSettingsTranslator.translate(foundUserSettings))
    }

    override fun updateUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId!!
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound("User settings not found!")

        val homeAddress = userSettings.homeAddress
        val updatedUserSettings = usersController.updateUserSettings(
            foundUserSettings,
            homeAddress?.streetAddress,
            homeAddress?.postalCode,
            homeAddress?.city,
            homeAddress?.country,
            userSettings.showMobileWelcomeScreen,
            userSettings.pollutantPenalties,
            userSettings.pollutantThresholds,
            userId
        )

        return createOk(userSettingsTranslator.translate(updatedUserSettings))
    }

    override fun deleteUser(): Response {
        usersController.deleteUser(loggerUserId!!)
        return createNoContent()
    }

    override fun deleteUserSettings(): Response {
        usersController.deleteUserSettings(loggerUserId!!)
        return createNoContent()
    }

    override fun downloadUserData(): Response {
        return streamResponse(usersController.findUserData(loggerUserId!!), "application/zip")
    }

    /* TotalExposure */

    override fun totalExposure(exposedBefore: String?, exposedAfter: String?): Response {
        var exposedBeforeDate: OffsetDateTime? = null
        var exposedAfterDate: OffsetDateTime? = null

        if (exposedBefore != null) {
            exposedBeforeDate = OffsetDateTime.parse(exposedBefore)
        }

        if (exposedAfter != null) {
            exposedAfterDate = OffsetDateTime.parse(exposedAfter)
        }

        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.getTotalExposure(loggerUserId!!, exposedBeforeDate, exposedAfterDate)))
    }

    /* ExposureInstances */

    override fun createExposureInstance(exposureInstance: ExposureInstance): Response {
        var route: Route? = null
        if (exposureInstance.routeId != null) {
            route = routeController.findRoute(exposureInstance.routeId)
        }

        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.createExposureInstance(
            route,
            exposureInstance.startedAt,
            exposureInstance.endedAt,
            exposureInstance.carbonMonoxide,
            exposureInstance.nitrogenMonoxide,
            exposureInstance.nitrogenDioxide,
            exposureInstance.ozone,
            exposureInstance.sulfurDioxide,
            exposureInstance.harmfulMicroparticles,
            loggerUserId!!
        )))
    }

    override fun deleteExposureInstance(exposureInstanceId: UUID): Response {
        val exposureInstance = exposureInstanceController.findExposureInstance(exposureInstanceId) ?: return createNotFound(EXPOSURE_INSTANCE_NOT_FOUND)

        if (exposureInstance.creatorId != loggerUserId!!) {
            return createUnauthorized("You are unauthorized to delete this!")
        }

        exposureInstanceController.deleteExposureInstance(exposureInstance)

        return createNoContent()
    }

    override fun findExposureInstance(exposureInstanceId: UUID): Response {
        val exposureInstance = exposureInstanceController.findExposureInstance(exposureInstanceId) ?: return createNotFound(EXPOSURE_INSTANCE_NOT_FOUND)
        if (!exposureInstance.creatorId!!.equals(loggerUserId!!)) {
            return createNotFound(EXPOSURE_INSTANCE_NOT_FOUND)
        }
        return createOk(exposureInstanceTranslator.translate(exposureInstance))
    }

    override fun listExposureInstances(createdBefore: String?, createdAfter: String?): Response {
        var createdBeforeDate: OffsetDateTime? = null
        var createdAfterDate: OffsetDateTime? = null

        if (createdBefore != null) {
            createdBeforeDate = OffsetDateTime.parse(createdBefore)
        }

        if (createdAfter != null) {
            createdAfterDate = OffsetDateTime.parse(createdAfter)
        }
        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.listExposureInstances(loggerUserId!!, createdBeforeDate, createdAfterDate)))
    }

    /* Favourites */

    override fun createUserFavouriteLocation(favouries: FavouriteLocation): Response {
        return createOk(favouritesTranslator.translate(favouritesController.createFavourite(favouries.name, favouries.latitude, favouries.longitude, loggerUserId!!)))
    }

    override fun deleteUserFavouriteLocation(favouriteId: UUID): Response {
        val favourite = favouritesController.findFavourite(favouriteId) ?: return createBadRequest(ROUTE_NOT_FOUND)
        if (!favourite.creatorId!!.equals(loggerUserId!!)) {
            return createNotFound(ROUTE_NOT_FOUND)
        }
        favouritesController.deleteFavourite(favourite, loggerUserId!!)
        return createNoContent()
    }

    override fun updateUserFavouriteLocation(favouriteId: UUID, favouriteLocation: FavouriteLocation): Response {
        val userId = loggerUserId!!
        val updatedFavouriteLocation = favouritesController.updateFavourite(
            favouriteId,
            favouriteLocation.name,
            favouriteLocation.latitude,
            favouriteLocation.longitude,
            userId
        )

        return createOk(favouritesTranslator.translate(updatedFavouriteLocation))
    }

    override fun listUserFavouriteLocations(): Response {
        return createOk(favouritesTranslator.translate(favouritesController.listFavourites(loggerUserId!!)))
    }

    /* Routes */

    override fun createRoute(route: fi.metatavu.megasense.dataportal.api.spec.model.Route): Response {
        return createOk(routeTranslator.translate(routeController.createRoute(route.name, route.routePoints, route.locationFromName, route.locationToName, loggerUserId!!)))
    }

    override fun deleteRoute(routeId: UUID): Response {
        val route = routeController.findRoute(routeId) ?: return createBadRequest(ROUTE_NOT_FOUND)
        if (!route.creatorId!!.equals(loggerUserId!!)) {
            return createNotFound(ROUTE_NOT_FOUND)
        }
        routeController.deleteRoute(route, loggerUserId!!)
        return createNoContent()
    }

    override fun findRoute(routeId: UUID): Response {
        val route = routeController.findRoute(routeId) ?: return createNotFound(ROUTE_NOT_FOUND)
        if (!route.creatorId!!.equals(loggerUserId!!)) {
            return createNotFound("Route not found")
        }
        return createOk(routeTranslator.translate(route))
    }

    override fun listRoutes(): Response {
        return createOk(routeTranslator.translate(routeController.listRoutes(loggerUserId!!)))
    }

    /* System */

    override fun ping(): Response? {
        return Response.ok("pong").build()
    }

    companion object {
        private const val EXPOSURE_INSTANCE_NOT_FOUND = "Exposure instance not found"
        private const val ROUTE_NOT_FOUND = "Route not found!"
    }

}