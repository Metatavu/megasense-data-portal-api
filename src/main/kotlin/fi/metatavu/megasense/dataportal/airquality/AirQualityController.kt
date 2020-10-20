package fi.metatavu.megasense.dataportal.airquality

import fi.metatavu.megasense.dataportal.api.spec.model.AirQuality
import fi.metatavu.megasense.dataportal.api.spec.model.Location
import fi.metatavu.megasense.dataportal.api.spec.model.PollutantType
import org.geotools.referencing.GeodeticCalculator
import ucar.ma2.Array
import ucar.ma2.ArrayFloat
import ucar.nc2.NetcdfFile
import java.awt.geom.Point2D
import javax.enterprise.context.ApplicationScoped
import kotlin.math.abs


@ApplicationScoped
class AirQualityController {
    /**
     * Gets air quality for specific coordinates
     *
     * @param pollutant pollutant to get
     * @param coordinates coordinates
     *
     * @return air quality
     */
    fun getAirQualityForCoordinates (pollutant: String, coordinates: String): AirQuality {
        val netcdfFile = loadNetcdfFile()

        val timeArray = netcdfFile.findVariable("time").read()
        val latitudeArray = netcdfFile.findVariable("lat").read()
        val longitudeArray = netcdfFile.findVariable("lon").read()
        val latitude = coordinates.substringBefore(",").toFloat()
        val longitude = coordinates.substringAfter(",").toFloat()

        return if (pollutant == "MICRO_PARTICLES") {
            val pollutantValuesArray1 = netcdfFile.findVariable("daymax_cnc_PM10").read()
            val pollutantValuesArray2 = netcdfFile.findVariable("daymax_cnc_PM2_5").read()

            val pollutantValue1 = getClosestPollutantValue(latitudeArray, longitudeArray, timeArray, pollutantValuesArray1 as ArrayFloat.D4, latitude, longitude)
            val pollutantValue2 = getClosestPollutantValue(latitudeArray, longitudeArray, timeArray, pollutantValuesArray2 as ArrayFloat.D4, latitude, longitude)

            val combinedPollutantValue = pollutantValue1 + pollutantValue2
            constructAirQuality(combinedPollutantValue, pollutant, latitude, longitude)
        } else {
            val pollutantValuesArray = getPollutionValuesFromNetcdf(netcdfFile, pollutant)
            val pollutantValue = getClosestPollutantValue(latitudeArray, longitudeArray, timeArray, pollutantValuesArray as ArrayFloat.D4, latitude, longitude)
            constructAirQuality(pollutantValue, pollutant, latitude, longitude)
        }
    }

    /**
     * Returns air quality values
     *
     * @param pollutant return only values for this pollutant
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     *
     * @return air quality values
     */
    fun getAirQuality(pollutant: String, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val netcdfFile = loadNetcdfFile()

        val timeArray = netcdfFile.findVariable("time").read()
        val latitudeArray = netcdfFile.findVariable("lat").read()
        val longitudeArray = netcdfFile.findVariable("lon").read()

        if (pollutant == "MICRO_PARTICLES") {
            val pollutantValuesArray1 = netcdfFile.findVariable("daymax_cnc_PM10").read()
            val data1 = extractData(timeArray, latitudeArray, longitudeArray, pollutantValuesArray1 as ArrayFloat.D4, pollutant, boundingBoxCorner1, boundingBoxCorner2)

            val pollutantValuesArray2 = netcdfFile.findVariable("daymax_cnc_PM2_5").read()
            val data2 = extractData(timeArray, latitudeArray, longitudeArray, pollutantValuesArray2 as ArrayFloat.D4, pollutant, boundingBoxCorner1, boundingBoxCorner2)

            // Combining two different lists of particle observations into a single list
            val combined = data1 + data2
            val hashMap = HashMap<String, AirQuality>()
            for (airQuality in combined) {
                val latitude = airQuality.location.latitude
                val longitude = airQuality.location.longitude
                val locationString = "$latitude,$longitude"

                val found = hashMap[locationString]

                if (found == null) {
                    hashMap[locationString] = airQuality
                } else {
                    found.pollutionValue += airQuality.pollutionValue
                    hashMap[locationString] = found
                }
            }

            return hashMap.values.toList()
        } else {
            val pollutantValuesArray = getPollutionValuesFromNetcdf(netcdfFile, pollutant)
            return extractData(timeArray, latitudeArray, longitudeArray, pollutantValuesArray as ArrayFloat.D4, pollutant, boundingBoxCorner1, boundingBoxCorner2)
        }

    }

    /**
     * Loads the Netcdf-file for air quality data
     *
     * @return Netcdf-file
     */
    private fun loadNetcdfFile (): NetcdfFile {
        val resourceStream = javaClass.classLoader.getResource("fi/metatavu/megasense/dataportal/airquality.nc")!!.openStream()
        val bytes = resourceStream.readAllBytes()
        resourceStream.close()
        return NetcdfFile.openInMemory("airquality.nc", bytes)
    }

    /**
     * Gets pollution values from a Netcdf-file
     *
     * @param netcdfFile the file to load values from
     * @param pollutant the pollutant to load
     *
     * @return pollution values
     */
    private fun getPollutionValuesFromNetcdf (netcdfFile: NetcdfFile, pollutant: String): Array {
        when (pollutant) {
            "CARBON_MONOXIDE" -> {
                return netcdfFile.findVariable("daymax_cnc_CO").read()
            }

            "NITROGEN_MONOXIDE" -> {
                return netcdfFile.findVariable("daymax_cnc_NO").read()
            }

            "NITROGEN_DIOXIDE"-> {
                return netcdfFile.findVariable("daymax_cnc_NO2").read()
            }

            "OZONE" -> {
                return netcdfFile.findVariable("daymax_cnc_O3").read()
            }

            "SULFUR_DIOXIDE" -> {
                return netcdfFile.findVariable("daymax_cnc_SO2").read()
            }

            else -> {
                throw Error("Unrecognized pollutant $pollutant")
            }
        }
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
     *
     * @return air quality values
     */
    private fun extractData (timeArray: Array, latitudeArray: Array, longitudeArray: Array, pollutantValuesArray: ArrayFloat.D4, pollutant: String, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val airQualityList = ArrayList<AirQuality>()

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

     *
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

     *
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
     *
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
}