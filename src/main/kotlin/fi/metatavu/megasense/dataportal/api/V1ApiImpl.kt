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
import java.time.format.DateTimeParseException
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

    override fun getAirQualityForCoordinates(coordinates: String, pollutant: String): Response {
        return createOk(airQualityController.getAirQualityForCoordinates(pollutant, coordinates))
    }

    override fun getAirQuality(
        pollutant: String?,
        boundingBoxCorner1: String?,
        boundingBoxCorner2: String?,
        coordinatesArray: MutableList<String>?
    ): Response {
        return if (boundingBoxCorner1 != null && boundingBoxCorner2 != null && pollutant != null) {
            createOk(airQualityController.getAirQuality(pollutant, boundingBoxCorner1, boundingBoxCorner2))
        } else if (coordinatesArray != null) {
            createOk(airQualityController.getAirQualityList(pollutant, coordinatesArray))
        } else createBadRequest("Invalid parameters")
    }

    /* Users */

    override fun createUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
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
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound(getUserNotFoundError(userId))
        return createOk(userSettingsTranslator.translate(foundUserSettings))
    }

    override fun updateUserSettings(userSettings: UserSettings): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val foundUserSettings = usersController.findUserSettings(userId) ?: return createNotFound(getUserNotFoundError(userId))

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

    override fun deleteUserSettings(): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        usersController.deleteUserSettings(userId)
        return createNoContent()
    }

    override fun downloadUserData(): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return streamResponse(usersController.findUserData(userId), "application/zip")
    }

    /* TotalExposure */

    override fun totalExposure(exposedBefore: OffsetDateTime?, exposedAfter: OffsetDateTime?): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.getTotalExposure(userId, exposedBefore, exposedAfter)))
    }

    /* ExposureInstances */

    override fun createExposureInstance(exposureInstance: ExposureInstance): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        var route: Route? = null
        if (exposureInstance.routeId != null) {
            route = routeController.findRoute(exposureInstance.routeId)
            if (route == null) {
                return createBadRequest("Route not found!")
            }
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
            userId
        )))
    }

    override fun deleteExposureInstance(exposureInstanceId: UUID): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val exposureInstance = exposureInstanceController.findExposureInstance(exposureInstanceId) ?: return createNotFound(getExposureInstanceNotFoundError(exposureInstanceId))

        if (exposureInstance.creatorId != userId) {
            return createUnauthorized("You are unauthorized to delete this!")
        }

        exposureInstanceController.deleteExposureInstance(exposureInstance)

        return createNoContent()
    }

    override fun findExposureInstance(exposureInstanceId: UUID): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val exposureInstance = exposureInstanceController.findExposureInstance(exposureInstanceId) ?: return createNotFound(getExposureInstanceNotFoundError(exposureInstanceId))
        if (exposureInstance.creatorId!! != userId) {
            return createNotFound(getExposureInstanceNotFoundError(exposureInstanceId))
        }

        return createOk(exposureInstanceTranslator.translate(exposureInstance))
    }

    override fun listExposureInstances(createdBefore: OffsetDateTime?, createdAfter: OffsetDateTime?): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(exposureInstanceTranslator.translate(exposureInstanceController.listExposureInstances(userId, createdBefore, createdAfter)))
    }

    /* Favourites */

    override fun createUserFavouriteLocation(favouries: FavouriteLocation): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(favouritesTranslator.translate(favouritesController.createFavourite(favouries.name, favouries.latitude, favouries.longitude, userId)))
    }

    override fun deleteUserFavouriteLocation(favouriteId: UUID): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val favourite = favouritesController.findFavourite(favouriteId) ?: return createBadRequest(getFavouriteLocationNotFoundError(favouriteId))
        if (favourite.creatorId!! != userId) {
            return createNotFound(getFavouriteLocationNotFoundError(favouriteId))
        }

        favouritesController.deleteFavourite(favourite, userId)
        return createNoContent()
    }

    override fun updateUserFavouriteLocation(favouriteId: UUID, favouriteLocation: FavouriteLocation): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
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
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(favouritesTranslator.translate(favouritesController.listFavourites(userId)))
    }

    /* Routes */

    override fun createRoute(route: fi.metatavu.megasense.dataportal.api.spec.model.Route): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(routeTranslator.translate(routeController.createRoute(route.name, route.routePoints, route.locationFromName, route.locationToName, userId)))
    }

    override fun deleteRoute(routeId: UUID): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val route = routeController.findRoute(routeId) ?: return createBadRequest(getRouteNotFoundError(routeId))
        if (route.creatorId!! != userId) {
            return createNotFound(getRouteNotFoundError(routeId))
        }

        routeController.deleteRoute(route, userId)
        return createNoContent()
    }

    override fun findRoute(routeId: UUID): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        val route = routeController.findRoute(routeId) ?: return createNotFound(getRouteNotFound(routeId))
        if (route.creatorId!! != userId) {
            return createNotFound(getRouteNotFound(routeId))
        }

        return createOk(routeTranslator.translate(route))
    }

    override fun listRoutes(): Response {
        val userId = loggerUserId ?: return createUnauthorized(UNAUTHORIZED)
        return createOk(routeTranslator.translate(routeController.listRoutes(userId)))
    }

    /* System */

    override fun ping(): Response? {
        return Response.ok("pong").build()
    }

    companion object {

        /**
         * Gets favourite location not found error string
         *
         * @param uuid favourite location uuid
         * @return error string with favourite location uuid included
         */
        fun getFavouriteLocationNotFoundError(uuid: UUID): String {
            return String.format("Favourite location %s not found", uuid)
        }

        /**
         * Gets route not found error string
         *
         * @param uuid route uuid
         * @return error string with route uuid included
         */
        fun getRouteNotFoundError(uuid: UUID): String {
            return String.format("Route %s not found", uuid)
        }

        /**
         * Gets exposure instance not found error string
         *
         * @param uuid exposure instance uuid
         * @return error string with exposureInstance uuid included
         */
        fun getUserNotFoundError(uuid: UUID): String {
            return String.format("User %s not found", uuid)
        }

        /**
         * Gets exposure instance not found error string
         *
         * @param uuid exposure instance uuid
         * @return error string with exposureInstance uuid included
         */
        fun getExposureInstanceNotFoundError(uuid: UUID): String {
            return String.format("Exposure instance %s not found", uuid)
        }

        /**
         * Get route not found error string
         *
         * @param uuid route uuid
         * @return error string with route uuid included
         */
        fun getRouteNotFound(uuid: UUID): String {
            return String.format("Route %s not found", uuid)
        }
    }

}