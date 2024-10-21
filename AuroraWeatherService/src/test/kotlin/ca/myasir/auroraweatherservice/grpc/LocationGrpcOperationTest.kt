package ca.myasir.auroraweatherservice.grpc

import ca.myasir.auroraweatherservice.bo.LocationBo
import ca.myasir.auroraweatherservice.lib.*
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_PLACE_ID
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_SEARCH_TEXT
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleCoordinates
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleLocationResult
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleWeatherData
import ca.myasir.auroraweatherservice.util.GrpcLocationCoordinates
import ca.myasir.auroraweatherservice.util.GrpcLocationResult
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
        val expectedResults = results.map(WeatherData::toGrpc)
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

    @Test
    fun `it should call LocationBo to get location hit results for the given search text`() {
        val results = listOf(createSampleLocationResult())
        val expectedResults = results.map(LocationResult::toGrpc)
        val request = createGetLocationsRequest()
        val response = createGetLocationsResponse(expectedResults)
        val mockedObserver: StreamObserver<GetLocationsResponse> = mockk()

        every { mockedLocationBo.searchForLocations(TEST_SEARCH_TEXT) } returns results
        justRun { mockedObserver.onNext(response) }
        justRun { mockedObserver.onCompleted() }

        operation.getLocations(request, mockedObserver)

        verify(exactly = 1) {
            mockedObserver.onNext(response)
            mockedObserver.onCompleted()
        }
    }

    @Test
    fun `it should error-out if search text is not provided`() {
        val request = createGetLocationsRequest("")
        val mockedObserver: StreamObserver<GetLocationsResponse> = mockk()

        justRun { mockedObserver.onError(any()) }
        justRun { mockedObserver.onCompleted() }

        operation.getLocations(request, mockedObserver)

        verify(exactly = 0) {
            mockedObserver.onNext(any())
        }
        verify(exactly = 1) {
            mockedObserver.onError(any())
        }
    }

    @Test
    fun `it should call LocationBo to get location coordinates for the given place Id`() {
        val results = createSampleCoordinates()
        val expectedResults = results.toGrpc()
        val request = createGetCoordinatesRequest()
        val response = createGetCoordinatesResponse(expectedResults)
        val mockedObserver: StreamObserver<GetLocationCoordinatesResponse> = mockk()

        every { mockedLocationBo.getCoordinates(TEST_PLACE_ID) } returns results
        justRun { mockedObserver.onNext(response) }
        justRun { mockedObserver.onCompleted() }

        operation.getLocationCoordinates(request, mockedObserver)

        verify(exactly = 1) {
            mockedObserver.onNext(response)
            mockedObserver.onCompleted()
        }
    }

    @Test
    fun `it should error-out if place id not provided`() {
        val request = createGetCoordinatesRequest("")
        val mockedObserver: StreamObserver<GetLocationCoordinatesResponse> = mockk()

        justRun { mockedObserver.onError(any()) }
        justRun { mockedObserver.onCompleted() }

        operation.getLocationCoordinates(request, mockedObserver)

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

    private fun createGetLocationsRequest(searchText: String = TEST_SEARCH_TEXT): GetLocationsRequest {
        return GetLocationsRequest.newBuilder()
            .setSearchText(searchText)
            .build()
    }

    private fun createGetLocationsResponse(data: List<GrpcLocationResult>): GetLocationsResponse {
        return GetLocationsResponse.newBuilder()
            .addAllResults(data)
            .build()
    }

    private fun createGetCoordinatesRequest(placeId: String = TEST_PLACE_ID.value): GetLocationCoordinatesRequest {
        return GetLocationCoordinatesRequest.newBuilder()
            .setPlaceId(placeId)
            .build()
    }

    private fun createGetCoordinatesResponse(coordinates: GrpcLocationCoordinates): GetLocationCoordinatesResponse {
        return GetLocationCoordinatesResponse.newBuilder()
            .setCoordinates(coordinates)
            .build()
    }
}
