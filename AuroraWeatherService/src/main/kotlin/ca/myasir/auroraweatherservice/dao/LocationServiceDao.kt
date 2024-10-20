package ca.myasir.auroraweatherservice.dao

import ca.myasir.auroraweatherservice.model.LocationResult

interface LocationServiceDao {

    /**
     * This method will search ALS for the given text, and return any hits.
     */
    fun getLocations(searchText: String): List<LocationResult>
}
