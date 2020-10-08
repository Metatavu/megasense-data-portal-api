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
     * Returns air quality values
     *
     * @param pollutant return only values for this pollutant
     * @param precision precision in meters for the returned data
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     *
     * @return air quality values
     */
    fun getAirQuality(pollutant: String, precision: Int, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val resourceStream = javaClass.classLoader.getResource("fi/metatavu/megasense/dataportal/airquality.nc")!!.openStream()
        val bytes = resourceStream.readAllBytes()
        resourceStream.close()
        val netcdfFile = NetcdfFile.openInMemory("airquality.nc", bytes)

        val timeArray = netcdfFile.findVariable("time").read()
        val latitudeArray = netcdfFile.findVariable("lat").read()
        val longitudeArray = netcdfFile.findVariable("lon").read()

        when (pollutant)
        {
            "CARBON_MONOXIDE" -> {
                val dataArray = netcdfFile.findVariable("daymax_cnc_CO").read()
                return extractData(timeArray, latitudeArray, longitudeArray, dataArray, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)
            }

            "NITROGEN_MONOXIDE" -> {
                val dataArray = netcdfFile.findVariable("daymax_cnc_NO").read()
                return extractData(timeArray, latitudeArray, longitudeArray, dataArray, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)
            }

            "NITROGEN_DIOXIDE"-> {
                val dataArray = netcdfFile.findVariable("daymax_cnc_NO2").read()
                return extractData(timeArray, latitudeArray, longitudeArray, dataArray, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)
            }

            "OZONE" -> {
                val dataArray = netcdfFile.findVariable("daymax_cnc_O3").read()
                return extractData(timeArray, latitudeArray, longitudeArray, dataArray, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)
            }

            "SULFUR_DIOXIDE" -> {
                val dataArray = netcdfFile.findVariable("daymax_cnc_SO2").read()
                return extractData(timeArray, latitudeArray, longitudeArray, dataArray, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)
            }

            "MICRO_PARTICLES" -> {
                val dataArray1 = netcdfFile.findVariable("daymax_cnc_PM10").read()
                val data1 = extractData(timeArray, latitudeArray, longitudeArray, dataArray1, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)

                val dataArray2 = netcdfFile.findVariable("daymax_cnc_PM2_5").read()
                val data2 = extractData(timeArray, latitudeArray, longitudeArray, dataArray2, pollutant, precision, boundingBoxCorner1, boundingBoxCorner2)

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
     * @param latitudeArray Netcdf array for longitude
     * @param pollutantValuesArray Netcdf array for pollutant values
     * @param pollutant type of the pollutant
     * @param precision precision in meters for the returned data
     * @param boundingBoxCorner1 lower left of the bounding box
     * @param boundingBoxCorner2 upper right of the bounding box
     *
     * @return air quality values
     */
    private fun extractData(timeArray: Array, latitudeArray: Array, longitudeArray: Array, pollutantValuesArray: Array, pollutant: String, precision: Int, boundingBoxCorner1: String, boundingBoxCorner2: String): List<AirQuality> {
        val airQualityList = ArrayList<AirQuality>()

        val minimalLatitude = boundingBoxCorner1.substringBefore(",")
        val minimalLongitude = boundingBoxCorner1.substringAfter(",")

        val maximalLatitude = boundingBoxCorner2.substringBefore(",")
        val maximalLongitude = boundingBoxCorner2.substringAfter(",")

        val azimuth = getAzimuth(minimalLongitude.toDouble(), minimalLatitude.toDouble(), minimalLongitude.toDouble(), maximalLatitude.toDouble())
        val distance = getDistance(minimalLongitude.toDouble(), minimalLatitude.toDouble(), minimalLongitude.toDouble(), maximalLatitude.toDouble())
        var i = 0.0
        while (i < distance / precision) {
            val startingPoint = moveTo(minimalLongitude.toDouble(), minimalLatitude.toDouble(), azimuth, precision * i)
            var j = 0.0
            val azimuthJ = getAzimuth(startingPoint.x, startingPoint.y, maximalLongitude.toDouble(), startingPoint.y)
            val distanceJ = getDistance(startingPoint.x, startingPoint.y, maximalLongitude.toDouble(), startingPoint.y)

            while (j < distanceJ / precision) {
                val samplePoint = moveTo(startingPoint.x, startingPoint.y, azimuthJ, precision * j)
                val pollutionValue = getClosestPollutantValue(latitudeArray, longitudeArray, timeArray, pollutantValuesArray as ArrayFloat.D4, samplePoint)

                val airQuality = constructAirQuality(pollutionValue, pollutant, samplePoint)
                airQualityList.add(airQuality)

                j++
            }

            i++
        }

        return airQualityList
    }

    /**
     * Constructs an object for air quality that can be returned in the HTTP-response
     *
     * @param pollutionValue pollution value
     * @param pollutant type of the pollutant
     * @param samplePoint location of the observation
     *
     * @return air quality
     */
    private fun constructAirQuality (pollutionValue: Float, pollutant: String, samplePoint: Point2D): AirQuality {
        val location = Location()
        location.latitude = samplePoint.y.toFloat()
        location.longitude = samplePoint.x.toFloat()
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
     * @param samplePoint location to get values from
     *
     * @return pollutant value
     */
    private fun getClosestPollutantValue(latitudeArray: Array, longitudeArray: Array, timeArray: Array, pollutantValuesArray: ArrayFloat.D4, samplePoint: Point2D): Float {
        val lon = samplePoint.x
        val lat = samplePoint.y
        val lonIndex = getClosestIndex(longitudeArray, lon.toFloat())
        val latIndex = getClosestIndex(latitudeArray, lat.toFloat())
        val timeSize = timeArray.size.toInt()
        val height = 0

        return pollutantValuesArray.get(timeSize - 1 , height, latIndex, lonIndex)
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

    /**
     * Returns a new location after moving using GeodeticCalculator
     *
     * @param longitude longitude for the starting location
     * @param latitude latitude for the starting location
     * @param azimuth azimuth value for direction
     * @param amount amount value for direction
     *
     * @return new location
     */
    private fun moveTo(longitude: Double, latitude: Double, azimuth: Double, amount: Double): Point2D {
        val geodeticCalculator = GeodeticCalculator()
        geodeticCalculator.setStartingGeographicPoint(longitude, latitude)
        geodeticCalculator.setDirection(azimuth, amount)
        return geodeticCalculator.destinationGeographicPoint
    }

    /**
     * Returns azimuth for given coordinates
     *
     * @param fromLongitude from longitude
     * @param fromLatitude from latitude
     * @param toLongitude to longitude
     * @param toLatitude to latitude
     * @return azimuth
     */
    private fun getAzimuth(fromLongitude: Double, fromLatitude: Double, toLongitude: Double, toLatitude: Double): Double {
        val geodeticCalculator = GeodeticCalculator()
        geodeticCalculator.setStartingGeographicPoint(fromLongitude, fromLatitude)
        geodeticCalculator.setDestinationGeographicPoint(toLongitude, toLatitude)
        return geodeticCalculator.azimuth
    }

    /**
     * Returns distance between given coordinates
     *
     * @param fromLongitude from longitude
     * @param fromLatitude from latitude
     * @param toLongitude to longitude
     * @param toLatitude to latitude
     * @return distance between given coordinates
     */
    private fun getDistance(fromLongitude: Double, fromLatitude: Double, toLongitude: Double, toLatitude: Double): Double {
        val geodeticCalculator = GeodeticCalculator()
        geodeticCalculator.setStartingGeographicPoint(fromLongitude, fromLatitude)
        geodeticCalculator.setDestinationGeographicPoint(toLongitude, toLatitude)
        return geodeticCalculator.orthodromicDistance
    }
}