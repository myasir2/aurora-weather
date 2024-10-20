package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.WeatherDataRequest
import ca.myasir.auroraweatherservice.dao.WeatherDataResponse
import ca.myasir.auroraweatherservice.dao.WeatherProviderDao
import ca.myasir.auroraweatherservice.exception.WeatherProviderException
import ca.myasir.auroraweatherservice.logger
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.service.ApiGatewayService
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude

class WeatherProviderDaoImpl(
    weatherApiUrl: String,
    xWeatherApiUrl: String,

    private val numForecastDays: Int,
    private val apiGatewayService: ApiGatewayService,
) : WeatherProviderDao {

    private val providerToApiConfig = mapOf(
        WeatherProvider.WEATHER_API to weatherApiUrl,
        WeatherProvider.X_WEATHER_API to xWeatherApiUrl
    )

    override fun getForecast(longitude: Longitude, latitude: Latitude, provider: WeatherProvider): List<WeatherData> {
        val apiUrl = providerToApiConfig[provider]
            ?: throw UnsupportedOperationException("Invalid provider provided")

        try {
            logger.info { "Querying WeatherProviderAPI at: $apiUrl" }

            val response = apiGatewayService.executePostRequest(
                WeatherDataRequest(longitude.value, latitude.value, numForecastDays),
                apiUrl,
                WeatherDataResponse::class.java
            )

            return response.forecast
        } catch (e: Exception) {
            logger.error { "Error occurred while getting forecast for $longitude $latitude" }

            throw WeatherProviderException(e)
        }
    }
}
