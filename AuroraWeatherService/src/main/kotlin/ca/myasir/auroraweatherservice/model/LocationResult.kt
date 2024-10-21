package ca.myasir.auroraweatherservice.model

import ca.myasir.auroraweatherservice.util.GrpcLocationResult
import ca.myasir.auroraweatherservice.util.PlaceId

/**
 * A class to represent location hits from ALS (AmazonLocationService).
 */
data class LocationResult(

    val placeId: PlaceId,
    val locationName: String
) {

    fun toGrpc(): GrpcLocationResult {
        return GrpcLocationResult.newBuilder()
            .setPlaceId(placeId.value)
            .setLocationName(locationName)
            .build()
    }
}
