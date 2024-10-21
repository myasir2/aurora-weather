package ca.myasir.auroraweatherservice.model

import ca.myasir.auroraweatherservice.util.GrpcLocationCoordinates
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude

data class Coordinates(

    val longitude: Longitude,
    val latitude: Latitude
) {

    fun toGrpc(): GrpcLocationCoordinates {
        return GrpcLocationCoordinates.newBuilder()
            .setLongitude(longitude.value)
            .setLatitude(latitude.value)
            .build()
    }
}
