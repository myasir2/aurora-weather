package ca.myasir.auroraweatherservice.grpc

import ca.myasir.auroraweatherservice.bo.LocationBo
import ca.myasir.auroraweatherservice.lib.*
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.util.GrpcWeatherProvider
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class LocationGrpcOperation(
    private val locationBo: LocationBo
) : AuroraWeatherServiceGrpc.AuroraWeatherServiceImplBase() {

    override fun getWeatherData(
        request: GetWeatherDataRequest,
        responseObserver: StreamObserver<GetWeatherDataResponse>
    ) {
        val longitude = Longitude(request.longitude)
        val latitude = Latitude(request.latitude)
        val provider = if (request.provider == GrpcWeatherProvider.NONE) {
            responseObserver.onError(IllegalArgumentException("Must provide weather provider for location"))

            return
        }
        else {
            WeatherProvider.valueOf(request.provider.name)
        }

        val weatherData = locationBo.getWeatherForecast(longitude, latitude, provider)
        val grpcWeatherData = weatherData.map(WeatherData::toGrpc)
        val response = GetWeatherDataResponse.newBuilder()
            .addAllForecast(grpcWeatherData)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getLocations(
        request: GetLocationsRequest,
        responseObserver: StreamObserver<GetLocationsResponse>
    ) {
        val searchText = request.searchText?.trim() ?: ""

        if (searchText.isEmpty()) {
            responseObserver.onError(IllegalArgumentException("Must provide search text for location"))

            return
        }

        val locationResults = locationBo.searchForLocations(searchText)
        val grpcLocationResults = locationResults.map(LocationResult::toGrpc)
        val response = GetLocationsResponse.newBuilder()
            .addAllResults(grpcLocationResults)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getLocationCoordinates(
        request: GetLocationCoordinatesRequest,
        responseObserver: StreamObserver<GetLocationCoordinatesResponse>
    ) {
        val placeId = request.placeId?.let(::PlaceId) ?: PlaceId("")

        if (placeId.value.isEmpty()) {
            responseObserver.onError(IllegalArgumentException("Place ID must not be null"))

            return
        }

        val coordinates = locationBo.getCoordinates(placeId).toGrpc()
        val response = GetLocationCoordinatesResponse.newBuilder()
            .setCoordinates(coordinates)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getWeatherProviders(request: Empty, responseObserver: StreamObserver<GetWeatherProvidersResponse>) {
        val providers = GrpcWeatherProvider.entries
            .filter { it != GrpcWeatherProvider.NONE && it != GrpcWeatherProvider.UNRECOGNIZED }
            .map(GrpcWeatherProvider::name).toList()

        val response = GetWeatherProvidersResponse.newBuilder()
            .addAllProviders(providers)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
