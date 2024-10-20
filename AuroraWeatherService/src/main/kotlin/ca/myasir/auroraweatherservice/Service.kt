package ca.myasir.auroraweatherservice

import ca.myasir.auroraweatherservice.dao.WeatherProviderDao
import ca.myasir.auroraweatherservice.lib.AuroraWeatherServiceGrpc
import ca.myasir.auroraweatherservice.lib.GetWeatherDataRequest
import ca.myasir.auroraweatherservice.lib.GetWeatherDataResponse
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class Service(
    private val weatherProviderDao: WeatherProviderDao
) : AuroraWeatherServiceGrpc.AuroraWeatherServiceImplBase() {

    override fun getWeatherData(
        request: GetWeatherDataRequest,
        responseObserver: StreamObserver<GetWeatherDataResponse>
    ) {
        val longitude = Longitude(request.longitude)
        val latitude = Latitude(request.latitude)
        val provider = WeatherProvider.valueOf(request.provider.name)

        weatherProviderDao.getForecast(longitude, latitude, provider)

        super.getWeatherData(request, responseObserver)
    }
}
