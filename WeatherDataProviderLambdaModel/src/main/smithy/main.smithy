$version: "1.0"

namespace ca.myasir.auroraweatherservice.weatherdataprovider

use aws.auth#sigv4
use aws.protocols#restJson1
use smithy.framework#ValidationException

@sigv4(name: "execute-api")
@restJson1
service WeatherDataProviderService {
    version: "2018-05-10",
    operations: [
        GetWeatherData
    ],
}

@http(code: 200, method: "POST", uri: "/weather")
operation GetWeatherData {
    input: GetWeatherDataRequest,
    output: GetWeatherDataResponse,
    errors: [ValidationException]
}

structure GetWeatherDataRequest {
    longitude: Double,
    latitude: Double
}

structure GetWeatherDataResponse {
    temperature: Double
}
