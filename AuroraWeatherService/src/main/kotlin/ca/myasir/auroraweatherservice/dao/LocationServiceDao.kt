package ca.myasir.auroraweatherservice.dao

import ca.myasir.auroraweatherservice.model.Coordinates
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.util.PlaceId

interface LocationServiceDao {

    /**
     * This method will search ALS for the given text, and return any hits.
     */
    fun getLocations(searchText: String): List<LocationResult>

    /**
     * This method will query ALS for the given placeId for its coordinates
     */
    fun getCoordinates(placeId: PlaceId): Coordinates
}
