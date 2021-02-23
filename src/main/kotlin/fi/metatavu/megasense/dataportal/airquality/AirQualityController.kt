package fi.metatavu.megasense.dataportal.airquality

import fi.metatavu.megasense.dataportal.api.spec.model.AirQuality
import fi.metatavu.megasense.dataportal.api.spec.model.Location
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantType
import fi.metatavu.megasense.dataportal.api.spec.model.RouteAirQuality
import org.eclipse.microprofile.config.ConfigProvider
import ucar.ma2.Array
import ucar.ma2.ArrayFloat
import ucar.nc2.NetcdfFile
import javax.enterprise.context.ApplicationScoped
import kotlin.math.abs

/**
 * A controller class for air quality data
 */
@ApplicationScoped
class AirQualityController {

    /**
     * Gets air quality for specific coordinates
     *
     * @param pollutant pollutant to get
     * @param coordinates coordinates
     * @return air quality
     */
    fun getAirQualityForCoordinates (pollutant: String, coordinates: String): AirQuality {
        val netcdfFile = loadNetcdfFile()

        try {
            val timeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_TIME")).read()
            val latitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LAT")).read()
            val longitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LON")).read()
            val latitude = coordinates.substringBefore(",").toFloat()
            val longitude = coordinates.substringAfter(",").toFloat()

            return if (pollutant == "MICRO_PARTICLES") {
                val pollutantValuesArray1 = netcdfFile.findVariable(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM10")).read()
                val pollutantValuesArray2 = netcdfFile.findVariable(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM2_5")).read()

                val pollutantValue1 = getClosestPollutantValue(
                    latitudeArray,
                    longitudeArray,
                    timeArray,
                    pollutantValuesArray1 as ArrayFloat.D4,
                    latitude,
                    longitude
                )
                val pollutantValue2 = getClosestPollutantValue(
                    latitudeArray,
                    longitudeArray,
                    timeArray,
                    pollutantValuesArray2 as ArrayFloat.D4,
                    latitude,
                    longitude
                )

                val combinedPollutantValue = pollutantValue1 + pollutantValue2
                constructAirQuality(combinedPollutantValue, pollutant, latitude, longitude)
            } else {
                val pollutantValuesArray = getPollutionValuesFromNetcdf(netcdfFile, pollutant)
                val pollutantValue = getClosestPollutantValue(
                    latitudeArray,
                    longitudeArray,
                    timeArray,
                    pollutantValuesArray as ArrayFloat.D4,
                    latitude,
                    longitude
                )
                constructAirQuality(pollutantValue, pollutant, latitude, longitude)
            }
        }
        finally {
            netcdfFile.close()
        }
    }

    /**
     * Returns air quality values
     *
     * @param pollutant return only values for this pollutant
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     * @return air quality values
     */
    fun getAirQuality(pollutant: String, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val netcdfFile = loadNetcdfFile()

        try {
            val timeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_TIME")).read()
            val latitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LAT")).read()
            val longitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LON")).read()

            if (pollutant == "MICRO_PARTICLES") {
                val pollutantValuesArray1 = netcdfFile.findVariable(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM10")).read()
                val data1 = extractData(
                    timeArray,
                    latitudeArray,
                    longitudeArray,
                    pollutantValuesArray1 as ArrayFloat.D4,
                    pollutant,
                    boundingBoxCorner1,
                    boundingBoxCorner2
                )

                val pollutantValuesArray2 = netcdfFile.findVariable(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM2_5")).read()
                val data2 = extractData(
                    timeArray,
                    latitudeArray,
                    longitudeArray,
                    pollutantValuesArray2 as ArrayFloat.D4,
                    pollutant,
                    boundingBoxCorner1,
                    boundingBoxCorner2
                )

                // Combining two different lists of particle observations into a single list
                val combined = data1 + data2
                val airQualityForLocation = mutableMapOf<String, AirQuality>()
                for (airQuality in combined) {
                    val latitude = airQuality.location.latitude
                    val longitude = airQuality.location.longitude
                    val locationString = "$latitude,$longitude"

                    val foundAirQuality = airQualityForLocation[locationString]

                    if (foundAirQuality == null) {
                        airQualityForLocation[locationString] = airQuality
                    } else {
                        foundAirQuality.pollutionValue += airQuality.pollutionValue
                        airQualityForLocation[locationString] = foundAirQuality
                    }
                }

                return airQualityForLocation.values.toList()
            } else {
                val pollutantValuesArray = getPollutionValuesFromNetcdf(netcdfFile, pollutant)
                return extractData(
                    timeArray,
                    latitudeArray,
                    longitudeArray,
                    pollutantValuesArray as ArrayFloat.D4,
                    pollutant,
                    boundingBoxCorner1,
                    boundingBoxCorner2
                )
            }
        }
        finally {
            netcdfFile.close()
        }
    }

    /**
     * Loads the Netcdf-file for air quality data
     *
     * @return Netcdf-file
     */
    private fun loadNetcdfFile(): NetcdfFile {
        return NetcdfFile.open(ConfigProvider.getConfig().getValue("megasense.airquality.path", String::class.java))
    }

    /**
     * Gets pollution values from a Netcdf-file
     *
     * @param netcdfFile the file to load values from
     * @param pollutant the pollutant to load
     * @return pollution values
     */
    private fun getPollutionValuesFromNetcdf (netcdfFile: NetcdfFile, pollutant: String): Array {
        val foundVariable = netcdfFile.findVariable(getAirQualityPollutantParameter(pollutant));

        if (foundVariable == null) {
            throw Error("Unrecognized pollutant $pollutant")
        }

        else return foundVariable.read()
    }

    /**
     * Extracts data for a specific pollutant
     *
     * @param timeArray Netcdf array for time
     * @param latitudeArray Netcdf array for latitude
     * @param longitudeArray Netcdf array for longitude
     * @param pollutantValuesArray Netcdf array for pollutant values
     * @param pollutant type of the pollutant
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     * @return air quality values
     */
    private fun extractData (timeArray: Array, latitudeArray: Array, longitudeArray: Array, pollutantValuesArray: ArrayFloat.D4, pollutant: String, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val airQualityList = mutableListOf<AirQuality>()

        val minimalLatitude = boundingBoxCorner1.substringBefore(",").toFloat()
        val minimalLongitude = boundingBoxCorner1.substringAfter(",").toFloat()

        val maximalLatitude = boundingBoxCorner2.substringBefore(",").toFloat()
        val maximalLongitude = boundingBoxCorner2.substringAfter(",").toFloat()

        val latitudes = latitudeArray.size.toInt()
        val longitudes = longitudeArray.size.toInt()
        val timeSize = timeArray.size.toInt()
        val height = 0
        for (i in 0 until latitudes) {
            val latitude = latitudeArray.getFloat(i)
            if (latitude > minimalLatitude && latitude < maximalLatitude) {
                for (j in 0 until longitudes) {
                    val longitude = longitudeArray.getFloat(j)
                    if (longitude > minimalLongitude && longitude < maximalLongitude) {
                        airQualityList.add(constructAirQuality(pollutantValuesArray.get(timeSize - 1 , height, i, j), pollutant, latitude, longitude))
                    }
                }
            }
        }
        return airQualityList
    }

    /**
     * Constructs an object for air quality that can be returned in the HTTP-response
     *
     * @param pollutionValue pollution value
     * @param pollutant type of the pollutant
     * @param latitude latitude of the observation
     * @param longitude longitude of the observation
     * @return air quality
     */
    private fun constructAirQuality (pollutionValue: Float, pollutant: String, latitude: Float, longitude: Float): AirQuality {
        val location = Location()
        location.latitude = latitude
        location.longitude = longitude

        val airQuality = AirQuality()
        airQuality.location = location
        airQuality.pollutionValue = pollutionValue
        airQuality.pollutant = PollutantType.fromValue(pollutant)

        return airQuality
    }

    /**
     * Returns the newest and closest pollutant value to a specified location
     *
     * @param timeArray Netcdf array for time
     * @param latitudeArray Netcdf array for latitude
     * @param latitudeArray Netcdf array for longitude
     * @param pollutantValuesArray Netcdf array for pollutant values
     * @param latitude the latitude of the location to get values from
     * @param longitude the longitude of the location to get values from
     * @return pollutant value
     */
    private fun getClosestPollutantValue(latitudeArray: Array, longitudeArray: Array, timeArray: Array, pollutantValuesArray: ArrayFloat.D4, latitude: Float, longitude: Float): Float {
        val latitudeIndex = getClosestIndex(latitudeArray, latitude)
        val longitudeIndex = getClosestIndex(longitudeArray, longitude)
        val timeSize = timeArray.size.toInt()
        val height = 0

        return pollutantValuesArray.get(timeSize - 1 , height, latitudeIndex, longitudeIndex)
    }

    /**
     * Gets the closest index to a specified value in a Netcdf array
     *
     * @param array A Netcdf array
     * @param value get the closest index to this value
     * @return the closest index
     */
    private fun getClosestIndex(array: Array, value: Float): Int {
        var distance = Double.MAX_VALUE
        for (i in 0 until array.size.toInt()) {
            val current: Double = array.getDouble(i)
            val currentDistance = abs(current - value)
            distance = if (currentDistance < distance) {
                currentDistance
            } else {
                return i - 1
            }
        }
        return (array.size - 1).toInt()
    }

    /**
     * Gets air quality variable name for Netcdf file
     *
     * @param airQualityParameterName name of air quality property
     * @return name of airQuality variable
     */
    private fun getAirQualityParameter (airQualityParameterName: String): String {
        return ConfigProvider.getConfig().getValue("megasense.airquality.parameters.$airQualityParameterName", String::class.java)
    }

    /**
     * Gets air quality variable name for Netcdf file
     *
     * @param airQualityParameterName name of air quality property
     * @return name of airQuality variable
     */
    private fun getAirQualityPollutantParameter (airQualityParameterName: String): String {
        return ConfigProvider.getConfig().getValue("megasense.airquality.parameters.pollutant.$airQualityParameterName", String::class.java)
    }

    /**
     * Gets air quality data for array of coordinates
     * 
     * @param coordinates list of coordinates
     * @return list of RouteAirQuality entries
     */
    fun getAirQualityList(coordinates: MutableList<String>): List<RouteAirQuality> {
        val airQualities = mutableListOf<RouteAirQuality>()
        val netcdfFile = loadNetcdfFile()

        try {
            val timeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_TIME")).read()
            val latitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LAT")).read()
            val longitudeArray = netcdfFile.findVariable(getAirQualityParameter("AIR_QUALITY_LON")).read()

            for (coordinatePair in coordinates) {
                val latitude = coordinatePair.substringBefore(",").toFloat()
                val longitude = coordinatePair.substringAfter(",").toFloat()

                val routeAirQuality = RouteAirQuality()
                val location = Location()
                location.latitude = latitude
                location.longitude = longitude

                routeAirQuality.location = location
                routeAirQuality.microParticlesPm10 = getVariableValue(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM10"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.microParticlesPm25 = getVariableValue(getAirQualityPollutantParameter("MICRO_PARTICLES.AIR_QUALITY_DAYMAX_PM2_5"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.carbonMonoxide = getVariableValue(getAirQualityPollutantParameter("CARBON_MONOXIDE"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.nitrogenMonoxide = getVariableValue(getAirQualityPollutantParameter("NITROGEN_MONOXIDE"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.nitrogenDioxide = getVariableValue(getAirQualityPollutantParameter("NITROGEN_DIOXIDE"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.ozone = getVariableValue(getAirQualityPollutantParameter("OZONE"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                routeAirQuality.sulfurDioxide = getVariableValue(getAirQualityPollutantParameter("SULFUR_DIOXIDE"), netcdfFile, latitudeArray, longitudeArray, timeArray, latitude, longitude)
                
                airQualities.add(routeAirQuality)
            }
        }
        finally {
            netcdfFile.close()
        }
        return airQualities
    }

    /**
     * Gets pollution values for variable
     *
     * @param variableName variableName
     * @param netcdfFile netcdfFile
     * @param latitudeArray latitudeArray
     * @param longitudeArray longitudeArray
     * @param timeArray timeArray
     * @param latitude latitude
     * @param longitude longitude
     * @return pollution value
     */
    private fun getVariableValue(variableName: String, netcdfFile: NetcdfFile, latitudeArray: Array, longitudeArray: Array, timeArray: Array, latitude: Float, longitude: Float): Float? {
        val pollutantVariable = netcdfFile.findVariable(variableName)
        if (pollutantVariable != null) {
            val pollutantArray = pollutantVariable.read()
            return getClosestPollutantValue(
                latitudeArray,
                longitudeArray,
                timeArray,
                pollutantArray as ArrayFloat.D4,
                latitude,
                longitude
            )
        }

        return null
    }
}