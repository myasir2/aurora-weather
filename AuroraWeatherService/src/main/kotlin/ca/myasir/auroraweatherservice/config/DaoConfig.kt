package ca.myasir.auroraweatherservice.config

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.dao.impl.LocationServiceDaoImpl
import ca.myasir.auroraweatherservice.util.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.location.LocationClient

@Component
class DaoConfig {

    @Bean
    fun getLocationServiceDao(locationClient: LocationClient): LocationServiceDao {
        val indexName = EnvironmentUtils.extractEnvironmentVariable("LOCATION_SERVICE_INDEX_NAME")

        return LocationServiceDaoImpl(indexName, locationClient)
    }
}
