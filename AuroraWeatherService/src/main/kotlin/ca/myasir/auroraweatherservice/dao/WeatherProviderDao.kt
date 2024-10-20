package ca.myasir.auroraweatherservice.dao

import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude

interface WeatherProviderDao {

    /**
     * This method will query a weather provider API with the given lon/lat to get a forecast
     * for the given number of days
     */
    fun getForecast(longitude: Longitude, latitude: Latitude, provider: WeatherProvider): List<WeatherData>
}

/**
 * This class represents the structure for the WeatherDataProvider API request payload. It must conform to the
 * "GetWeatherDataRequest" Smithy model
 */
internal data class WeatherDataRequest(

    val longitude: Double,
    val latitude: Double,
    val numForecastDays: Int
)

/**
 * This class represents the structure for the WeatherDataProvider API response payload. It must conform to the
 * "GetWeatherDataResponse" Smithy model
 */
internal data class WeatherDataResponse(

    val forecast: List<WeatherData>
)
