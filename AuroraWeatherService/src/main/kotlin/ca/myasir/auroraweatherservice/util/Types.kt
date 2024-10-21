package ca.myasir.auroraweatherservice.util

@JvmInline
value class PlaceId(val value: String)

@JvmInline
value class Longitude(val value: Double)

@JvmInline
value class Latitude(val value: Double)

@JvmInline
value class Temperature(val value: Double)

typealias GrpcWeatherData = ca.myasir.auroraweatherservice.lib.WeatherData
typealias GrpcWeatherProvider = ca.myasir.auroraweatherservice.lib.WeatherProvider
typealias GrpcLocationResult = ca.myasir.auroraweatherservice.lib.LocationResult
typealias GrpcLocationCoordinates = ca.myasir.auroraweatherservice.lib.Coordinates
