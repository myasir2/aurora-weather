package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.exception.PlaceNotFoundException
import ca.myasir.auroraweatherservice.logger
import ca.myasir.auroraweatherservice.model.Coordinates
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId
import software.amazon.awssdk.services.location.LocationClient
import software.amazon.awssdk.services.location.model.GetPlaceRequest
import software.amazon.awssdk.services.location.model.ResourceNotFoundException
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForSuggestionsRequest

class LocationServiceDaoImpl(

    private val indexName: String,
    private val client: LocationClient
) : LocationServiceDao {

    override fun getLocations(searchText: String): List<LocationResult> {
        val request = SearchPlaceIndexForSuggestionsRequest.builder()
            .indexName(indexName)
            .text(searchText)
            .maxResults(MAX_RESULTS)
            .filterCategories(FILTER_CATEGORIES)
            .build()

        val response = client.searchPlaceIndexForSuggestions(request)

        // No need to check for null as according to the docs, results() will never return null
        return response.results().filter { it.placeId() != null }.map {
            val locationName = it.text()

            LocationResult(
                PlaceId(it.placeId()),
                locationName,
            )
        }
    }

    override fun getCoordinates(placeId: PlaceId): Coordinates {
        val request = GetPlaceRequest.builder()
            .indexName(indexName)
            .placeId(placeId.value)
            .build()

        try {
            val response = client.getPlace(request)
            val coordinates = response.place().geometry().point()

            logger.info { "Coordinates for $placeId: $coordinates" }

            return Coordinates(
                Longitude(coordinates[1]),
                Latitude(coordinates[0]),
            )
        }
        catch (e: ResourceNotFoundException) {
            logger.error { "Place $placeId not found in ALS" }

            throw PlaceNotFoundException(e)
        }
    }

    private companion object {

        // By default, the ALS API returns 5 results. 15 is a good number to show to the user
        const val MAX_RESULTS = 15

        // To ensure we only get cities/regions in the search hit results
        val FILTER_CATEGORIES = listOf("MunicipalityType")
    }
}
