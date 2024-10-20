package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_COUNTRY
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LATITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_LONGITUDE
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_MUNICIPALITY
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_PLACE_ID
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_REGION
import ca.myasir.auroraweatherservice.test_util.TestDefaults.TEST_SEARCH_TEXT
import ca.myasir.auroraweatherservice.test_util.TestDefaults.createSampleLocationResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
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

        every { client.searchPlaceIndexForText(request) } returns response

        val actualLocationResults = dao.getLocations(TEST_SEARCH_TEXT)

        assertEquals(expectedLocationResults, actualLocationResults)
    }

    private fun createSearchRequest(): SearchPlaceIndexForTextRequest {
        return SearchPlaceIndexForTextRequest.builder()
            .indexName(indexName)
            .text(TEST_SEARCH_TEXT)
            .build()
    }

    private fun createSearchResponse(): SearchPlaceIndexForTextResponse {
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
        val result = SearchForTextResult.builder()
            .place(place)
            .placeId(TEST_PLACE_ID.value)
            .build()

        return SearchPlaceIndexForTextResponse.builder()
            .results(result)
            .build()
    }
}
