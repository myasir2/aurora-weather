package ca.myasir.auroraweatherservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class AppConfig(

    @Value("\${numForecastDays}")
    val numForecastDays: Int,
)
