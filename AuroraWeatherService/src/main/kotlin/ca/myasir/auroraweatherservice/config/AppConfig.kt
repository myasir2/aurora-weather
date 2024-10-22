package ca.myasir.auroraweatherservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class AppConfig(

    @Value("\${numForecastDays:3}")
    var numForecastDays: Int = 3,
)
