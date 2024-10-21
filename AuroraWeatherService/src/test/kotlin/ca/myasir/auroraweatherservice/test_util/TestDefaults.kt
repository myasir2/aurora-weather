package ca.myasir.auroraweatherservice.test_util

import ca.myasir.auroraweatherservice.model.Coordinates
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId
import ca.myasir.auroraweatherservice.util.Temperature
import java.time.Instant

object TestDefaults {

    const val TEST_SEARCH_TEXT = "searchText"
    const val TEST_MUNICIPALITY = "Toronto"
    const val TEST_REGION = "ON"
    const val TEST_COUNTRY = "Canada"
    const val TEST_NUM_FORECAST_DAYS = 3
    const val TEST_WIND_SPEED = 60.0
    const val TEST_WIND_DIRECTION = 270.0
    const val TEST_HUMIDITY = 20.0
    const val TEST_DEWPOINT = 10.0
    const val TEST_UV = 2.0
    const val TEST_VISIBILITY = 10.0
    const val TEST_WEATHER_ICON_URL = "icon.png"
    const val TEST_LOCATION_LABEL = "location label"
    val TEST_PLACE_ID = PlaceId("placeId")
    val TEST_LONGITUDE = Longitude(43.6532)
    val TEST_LATITUDE = Latitude(-79.3832)
    val TEST_DATE = Instant.EPOCH
    val TEST_TEMP = Temperature(20.9)
    val TEST_MIN_TEMP = Temperature(10.0)
    val TEST_MAX_TEMP = Temperature(30.0)

    fun createSampleLocationResult(): LocationResult {
        return LocationResult(
            TEST_PLACE_ID,
            TEST_LOCATION_LABEL
        )
    }

    fun createSampleCoordinates(): Coordinates {
        return Coordinates(
            TEST_LONGITUDE,
            TEST_LATITUDE
        )
    }

    fun createSampleWeatherData(): WeatherData {
        return WeatherData(
            TEST_DATE,
            TEST_TEMP,
            TEST_MIN_TEMP,
            TEST_MAX_TEMP,
            TEST_WIND_SPEED,
            TEST_WIND_DIRECTION,
            TEST_HUMIDITY,
            TEST_DEWPOINT,
            TEST_UV,
            TEST_VISIBILITY,
            TEST_WEATHER_ICON_URL
        )
    }
}
