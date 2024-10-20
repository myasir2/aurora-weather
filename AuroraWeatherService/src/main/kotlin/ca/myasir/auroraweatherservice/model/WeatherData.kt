package ca.myasir.auroraweatherservice.model

import ca.myasir.auroraweatherservice.util.Temperature
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
    val uv: Int,
    val visibility: Int,
    val weatherIconUrl: String
)
