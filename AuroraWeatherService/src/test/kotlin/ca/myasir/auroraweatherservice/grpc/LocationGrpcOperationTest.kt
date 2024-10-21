package ca.myasir.auroraweatherservice.grpc

import ca.myasir.auroraweatherservice.bo.LocationBo
import ca.myasir.auroraweatherservice.lib.GetWeatherDataRequest
import ca.myasir.auroraweatherservice.lib.GetWeatherDataResponse
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleWeatherData
import ca.myasir.auroraweatherservice.util.GrpcWeatherData
import ca.myasir.auroraweatherservice.util.GrpcWeatherProvider
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LocationGrpcOperationTest {

    private val mockedLocationBo: LocationBo = mockk()
    private val operation = LocationGrpcOperation(mockedLocationBo)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `it should call LocationBo to get weather forecast for the given location`() {
        val results = listOf(createSampleWeatherData())
        val expectedResults = results.map(WeatherData::toGrpcWeatherData)
        val request = createGetWeatherDataRequest()
        val response = createGetWeatherDataResponse(expectedResults)
        val mockedObserver: StreamObserver<GetWeatherDataResponse> = mockk()

        every {
            mockedLocationBo.getWeatherForecast(TEST_LONGITUDE, TEST_LATITUDE, WeatherProvider.WEATHER_API)
        } returns results
        justRun { mockedObserver.onNext(response) }
        justRun { mockedObserver.onCompleted() }

        operation.getWeatherData(request, mockedObserver)

        verify(exactly = 1) {
            mockedObserver.onNext(response)
            mockedObserver.onCompleted()
        }
    }

    @Test
    fun `it should error-out if provider is NONE (not provided)`() {
        val request = createGetWeatherDataRequest(GrpcWeatherProvider.NONE)
        val mockedObserver: StreamObserver<GetWeatherDataResponse> = mockk()

        justRun { mockedObserver.onError(any()) }
        justRun { mockedObserver.onCompleted() }

        operation.getWeatherData(request, mockedObserver)

        verify(exactly = 0) {
            mockedObserver.onNext(any())
        }
        verify(exactly = 1) {
            mockedObserver.onError(any())
        }
    }

    private fun createGetWeatherDataRequest(
        provider: GrpcWeatherProvider = GrpcWeatherProvider.WEATHER_API
    ): GetWeatherDataRequest {
        return GetWeatherDataRequest.newBuilder()
            .setLongitude(TEST_LONGITUDE.value)
            .setLatitude(TEST_LATITUDE.value)
            .setProvider(provider)
            .build()
    }

    private fun createGetWeatherDataResponse(data: List<GrpcWeatherData>): GetWeatherDataResponse {
        return GetWeatherDataResponse.newBuilder()
            .addAllForecast(data)
            .build()
    }
}
