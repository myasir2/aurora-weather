package ca.myasir.auroraweatherservice.dao.impl

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId
import software.amazon.awssdk.services.location.LocationClient
import software.amazon.awssdk.services.location.model.Place
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextRequest

class LocationServiceDaoImpl(

    private val indexName: String,
    private val client: LocationClient
) : LocationServiceDao {

    override fun getLocations(searchText: String): List<LocationResult> {
        val request = SearchPlaceIndexForTextRequest.builder()
            .indexName(indexName)
            .text(searchText)
            .build()

        val response = client.searchPlaceIndexForText(request)

        // No need to check for null as according to the docs, results() will never return null
        return response.results().map {
            val locationName = createLocationName(it.place())
            val coordinates = it.place().geometry().point()

            LocationResult(
                PlaceId(it.placeId()),
                locationName,
                Longitude(coordinates[0]),
                Latitude(coordinates[1])
            )
        }
    }

    /**
     * This method will create a locationName for the given Place. It will combine the city, state, and country.
     */
    private fun createLocationName(place: Place): String {
        return listOf(
            place.municipality(),
            place.region(),
            place.country()
        ).joinToString(", ")
    }
}
