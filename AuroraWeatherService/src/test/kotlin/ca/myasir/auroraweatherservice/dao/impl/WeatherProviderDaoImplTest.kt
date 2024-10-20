package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.WeatherDataRequest
import ca.myasir.auroraweatherservice.dao.WeatherDataResponse
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.service.ApiGatewayService
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_NUM_FORECAST_DAYS
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleWeatherData
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class WeatherProviderDaoImplTest {

    private val weatherApiUrl = "/weather-api"
    private val xWeatherApiUrl = "/x-weather"
    private val mockedApiService = mockk<ApiGatewayService>()
    private val dao = WeatherProviderDaoImpl(weatherApiUrl, xWeatherApiUrl, TEST_NUM_FORECAST_DAYS, mockedApiService)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `it should call WeatherAPI to retrieve forecast successfully`() {
        val expectedForecast = listOf(createSampleWeatherData())
        val request = createWeatherDataRequest()
        val response = createWeatherDataResponse(expectedForecast)

        every {
            mockedApiService.executePostRequest(request, weatherApiUrl, WeatherDataResponse::class.java)
        } returns response

        val actualForecast = dao.getForecast(TEST_LONGITUDE, TEST_LATITUDE, WeatherProvider.WEATHER_API)

        assertEquals(expectedForecast, actualForecast)
    }

    @Test
    fun `it should call XWeatherAPI to retrieve forecast successfully`() {
        val expectedForecast = listOf(createSampleWeatherData())
        val request = createWeatherDataRequest()
        val response = createWeatherDataResponse(expectedForecast)

        every {
            mockedApiService.executePostRequest(request, xWeatherApiUrl, WeatherDataResponse::class.java)
        } returns response

        val actualForecast = dao.getForecast(TEST_LONGITUDE, TEST_LATITUDE, WeatherProvider.X_WEATHER_API)

        assertEquals(expectedForecast, actualForecast)
    }

    private fun createWeatherDataRequest(): WeatherDataRequest {
        return WeatherDataRequest(TEST_LONGITUDE.value, TEST_LATITUDE.value, TEST_NUM_FORECAST_DAYS)
    }

    private fun createWeatherDataResponse(forecast: List<WeatherData>): WeatherDataResponse {
        return WeatherDataResponse(forecast)
    }
}
