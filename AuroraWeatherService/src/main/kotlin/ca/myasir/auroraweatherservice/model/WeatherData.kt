package ca.myasir.auroraweatherservice.model

import ca.myasir.auroraweatherservice.util.GrpcWeatherData
import ca.myasir.auroraweatherservice.util.Temperature
import ca.myasir.auroraweatherservice.util.toIsoFormat
import java.time.Instant

data class WeatherData(

    val date: Instant,
    val temp: Temperature,
    val minTemp: Temperature,
    val maxTemp: Temperature,
    val windSpeed: Double,
    val windDirectionDegree: Double,
    val humidity: Double,
    val dewpoint: Double,
    val uv: Double,
    val visibility: Double,
    val weatherIconUrl: String
) {

    fun toGrpcWeatherData(): GrpcWeatherData {
        return GrpcWeatherData.newBuilder()
            .setDate(date.toIsoFormat())
            .setTemp(temp.value)
            .setMinTemp(minTemp.value)
            .setMaxTemp(maxTemp.value)
            .setWindSpeed(windSpeed)
            .setWindDirectionDegree(windDirectionDegree)
            .setHumidity(humidity)
            .setDewpoint(dewpoint)
            .setUv(uv)
            .setVisibility(visibility)
            .setWeatherIconUrl(weatherIconUrl)
            .build()
    }
}
