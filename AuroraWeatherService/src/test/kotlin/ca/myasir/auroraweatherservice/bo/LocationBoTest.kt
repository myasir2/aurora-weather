package ca.myasir.auroraweatherservice.bo

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.dao.WeatherProviderDao
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_PLACE_ID
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleCoordinates
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleLocationResult
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleWeatherData
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LocationBoTest {

    private val testSearchText = "searchText"
    private val mockedLocationServiceDao: LocationServiceDao = mockk()
    private val mockedWeatherProviderDao: WeatherProviderDao = mockk()
    private val bo = LocationBo(mockedLocationServiceDao, mockedWeatherProviderDao)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `it should call ALS DAO to get location hits for the given search text`() {
        val expectedResults = listOf(createSampleLocationResult())

        every { mockedLocationServiceDao.getLocations(testSearchText) } returns expectedResults

        val actualResults = bo.searchForLocations(testSearchText)

        assertEquals(expectedResults, actualResults)
    }

    @Test
    fun `it should call ALS DAO to get coordinates for the given place id`() {
        val expectedCoordinates = createSampleCoordinates()

        every { mockedLocationServiceDao.getCoordinates(TEST_PLACE_ID) } returns expectedCoordinates

        val actualCoordinates = bo.getCoordinates(TEST_PLACE_ID)

        assertEquals(expectedCoordinates, actualCoordinates)
    }

    @Test
    fun `it should call weather provider DAO to get forecast for the given location`() {
        val expectedResults = listOf(createSampleWeatherData())

        every {
            mockedWeatherProviderDao.getForecast(TEST_LONGITUDE, TEST_LATITUDE, WeatherProvider.WEATHER_API)
        } returns expectedResults

        val actualResults = bo.getWeatherForecast(TEST_LONGITUDE, TEST_LATITUDE, WeatherProvider.WEATHER_API)

        assertEquals(expectedResults, actualResults)
    }
}
