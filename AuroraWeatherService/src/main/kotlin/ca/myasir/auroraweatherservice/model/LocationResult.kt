package ca.myasir.auroraweatherservice.model

import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId

/**
 * A class to represent location hits from ALS (AmazonLocationService).
 */
data class LocationResult(

    val placeId: PlaceId,
    val locationName: String,
    val longitude: Longitude,
    val latitude: Latitude,
)
