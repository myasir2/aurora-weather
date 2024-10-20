package ca.myasir.auroraweatherservice.test_util

import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.util.PlaceId

object TestDefaults {

    const val TEST_SEARCH_TEXT = "searchText"
    const val TEST_LONGITUDE = 43.6532
    const val TEST_LATITUDE = -79.3832
    const val TEST_MUNICIPALITY = "Toronto"
    const val TEST_REGION = "ON"
    const val TEST_COUNTRY = "Canada"
    val TEST_PLACE_ID = PlaceId("placeId")

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
