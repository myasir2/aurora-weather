package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.exception.PlaceNotFoundException
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_COUNTRY
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LOCATION_LABEL
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_MUNICIPALITY
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_PLACE_ID
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_REGION
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_SEARCH_TEXT
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleCoordinates
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleLocationResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.location.LocationClient
import software.amazon.awssdk.services.location.model.*
import kotlin.test.assertEquals

class LocationServiceDaoImplTest {

    private val indexName = "indexName"
    private val client = mockk<LocationClient>()
    private val dao = LocationServiceDaoImpl(indexName, client)

    @Test
    fun `it should call LocationService to search for given text and return results`() {
        val request = createSearchRequest()
        val response = createSearchResponse()
        val expectedLocationResults = listOf(createSampleLocationResult())

        every { client.searchPlaceIndexForSuggestions(request) } returns response

        val actualLocationResults = dao.getLocations(TEST_SEARCH_TEXT)

        assertEquals(expectedLocationResults, actualLocationResults)
    }

    @Test
    fun `it should call LocationService to get coordinates for a given placeId`() {
        val request = createGetPlaceRequest()
        val response = createGetPlaceResponse()
        val expectedCoordinates = createSampleCoordinates()

        every { client.getPlace(request) } returns response

        val actualCoordinates = dao.getCoordinates(TEST_PLACE_ID)

        assertEquals(expectedCoordinates, actualCoordinates)
    }

    @Test
    fun `it should throw PlaceNotFoundException if place is not found in ALS`() {
        val request = createGetPlaceRequest()

        every { client.getPlace(request) }
            .throws(ResourceNotFoundException.builder().message("Not found").build())

        assertThrows<PlaceNotFoundException> {
            dao.getCoordinates(TEST_PLACE_ID)
        }
    }

    private fun createSearchRequest(): SearchPlaceIndexForSuggestionsRequest {
        return SearchPlaceIndexForSuggestionsRequest.builder()
            .indexName(indexName)
            .text(TEST_SEARCH_TEXT)
            .maxResults(15)
            .filterCategories(listOf("MunicipalityType"))
            .build()
    }

    private fun createSearchResponse(): SearchPlaceIndexForSuggestionsResponse? {
        val result = SearchForSuggestionsResult.builder()
            .placeId(TEST_PLACE_ID.value)
            .text(TEST_LOCATION_LABEL)
            .build()

        return SearchPlaceIndexForSuggestionsResponse.builder()
            .results(result)
            .build()
    }

    private fun createGetPlaceRequest(): GetPlaceRequest {
        return GetPlaceRequest.builder()
            .indexName(indexName)
            .placeId(TEST_PLACE_ID.value)
            .build()
    }

    private fun createGetPlaceResponse(): GetPlaceResponse {
        val place = Place.builder()
            .municipality(TEST_MUNICIPALITY)
            .region(TEST_REGION)
            .country(TEST_COUNTRY)
            .geometry(
                PlaceGeometry.builder()
                    .point(TEST_LONGITUDE.value, TEST_LATITUDE.value)
                    .build()
            )
            .build()

        return GetPlaceResponse.builder()
            .place(place)
            .build()
    }
}
