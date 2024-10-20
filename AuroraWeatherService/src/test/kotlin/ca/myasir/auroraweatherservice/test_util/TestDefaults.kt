package ca.myasir.auroraweatherservice.test_util

import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId

object TestDefaults {

    const val TEST_SEARCH_TEXT = "searchText"
    const val TEST_MUNICIPALITY = "Toronto"
    const val TEST_REGION = "ON"
    const val TEST_COUNTRY = "Canada"
    val TEST_PLACE_ID = PlaceId("placeId")
    val TEST_LONGITUDE = Longitude(43.6532)
    val TEST_LATITUDE = Latitude(-79.3832)

    fun createSampleLocationResult(): LocationResult {
        return LocationResult(
            TEST_PLACE_ID,
            listOf(
                TEST_MUNICIPALITY,
                TEST_REGION,
                TEST_COUNTRY
            ).joinToString(", "),
            TEST_LONGITUDE,
            TEST_LATITUDE
        )
    }
}
