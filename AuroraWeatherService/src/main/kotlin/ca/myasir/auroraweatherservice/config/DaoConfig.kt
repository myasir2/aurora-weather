package ca.myasir.auroraweatherservice.config

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.dao.WeatherProviderDao
import ca.myasir.auroraweatherservice.dao.impl.LocationServiceDaoImpl
import ca.myasir.auroraweatherservice.dao.impl.WeatherProviderDaoImpl
import ca.myasir.auroraweatherservice.service.ApiGatewayService
import ca.myasir.auroraweatherservice.util.EnvironmentUtils
import com.google.gson.Gson
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.location.LocationClient

@Component
class DaoConfig {

    @Bean
    fun getLocationServiceDao(locationClient: LocationClient): LocationServiceDao {
        val indexName = EnvironmentUtils.extractEnvironmentVariable("LOCATION_SERVICE_INDEX_NAME")

        return LocationServiceDaoImpl(indexName, locationClient)
    }

    @Bean
    fun getWeatherDataProviderDao(
        gson: Gson,
        region: Region,
        service: ApiGatewayService,
        appConfig: AppConfig
    ): WeatherProviderDao {
        val weatherApiUrl = EnvironmentUtils.extractEnvironmentVariable("WEATHER_API_URL")
        val xWeatherApiUrl = EnvironmentUtils.extractEnvironmentVariable("X_WEATHER_API_URL")

        return WeatherProviderDaoImpl(weatherApiUrl, xWeatherApiUrl, appConfig.numForecastDays, service)
    }
}
