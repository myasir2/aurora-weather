$version: "2.0"

namespace ca.myasir.auroraweatherservice.weatherdataprovider

use aws.api#service
use aws.apigateway#integration
use aws.apigateway#requestValidator
use aws.auth#sigv4
use aws.protocols#restJson1
use smithy.framework#ValidationException

@service(sdkId: "API Gateway", arnNamespace: "weatherdataproviderservice")
@restJson1
@sigv4(name: "weatherdataproviderservice")
@requestValidator("full")
service WeatherDataProviderService {
    version: "2018-05-10"
    operations: [
        GetWeatherApiData
        GetXWeatherApiData
    ]
}

@http(code: 200, method: "POST", uri: "/weather-api")
@integration(type: "aws_proxy", httpMethod: "POST", uri: "WeatherApiHandler", passThroughBehavior: "when_no_match")
operation GetWeatherApiData {
    input: GetWeatherDataRequest
    output: GetWeatherDataResponse
    errors: [
        ValidationException
    ]
}

@http(code: 200, method: "POST", uri: "/x-weather")
@integration(type: "aws_proxy", httpMethod: "POST", uri: "XWeatherHandler", passThroughBehavior: "when_no_match")
operation GetXWeatherApiData {
    input: GetWeatherDataRequest
    output: GetWeatherDataResponse
    errors: [
        ValidationException
    ]
}

structure GetWeatherDataRequest {
    @required
    longitude: Double
    @required
    latitude: Double
}

structure GetWeatherDataResponse {
    forecast: WeatherDataList
}

list WeatherDataList {
    member: WeatherData
}

structure WeatherData {
    date: String
    temp: Double
    minTemp: Double
    maxTemp: Double
    windSpeed: Double
    windDirectionDegree: Double
    humidity: Integer
    dewpoint: Integer
    uv: Integer
    visibility: Integer
    weatherIconUrl: String
}
