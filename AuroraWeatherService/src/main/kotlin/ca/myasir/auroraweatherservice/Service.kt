package ca.myasir.auroraweatherservice

import ca.myasir.auroraweatherservice.lib.HelloReply
import ca.myasir.auroraweatherservice.lib.HelloRequest
import ca.myasir.auroraweatherservice.lib.MyServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class Service: MyServiceGrpc.MyServiceImplBase()  {

    override fun sayHello(request: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
        val reply = HelloReply.newBuilder()
            .setMessage("Hello, ${request.name}")
            .build()

        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}
