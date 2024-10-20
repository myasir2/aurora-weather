package ca.myasir.auroraweatherservice

import ca.myasir.auroraweatherservice.lib.AuroraWeatherServiceGrpc
import ca.myasir.auroraweatherservice.lib.GetWeatherDataRequest
import ca.myasir.auroraweatherservice.lib.GetWeatherDataResponse
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class Service : AuroraWeatherServiceGrpc.AuroraWeatherServiceImplBase() {

    override fun getWeatherData(
        request: GetWeatherDataRequest?,
        responseObserver: StreamObserver<GetWeatherDataResponse>?
    ) {
        super.getWeatherData(request, responseObserver)
    }
}
