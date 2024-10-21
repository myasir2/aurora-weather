package ca.myasir.auroraweatherservice.grpc

import ca.myasir.auroraweatherservice.bo.LocationBo
import ca.myasir.auroraweatherservice.lib.AuroraWeatherServiceGrpc
import ca.myasir.auroraweatherservice.lib.GetWeatherDataRequest
import ca.myasir.auroraweatherservice.lib.GetWeatherDataResponse
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.util.GrpcWeatherProvider
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
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
        val grpcWeatherData = weatherData.map(WeatherData::toGrpcWeatherData)
        val response = GetWeatherDataResponse.newBuilder()
            .addAllForecast(grpcWeatherData)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
