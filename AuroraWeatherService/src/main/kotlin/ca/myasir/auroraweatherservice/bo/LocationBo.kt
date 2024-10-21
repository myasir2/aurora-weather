package ca.myasir.auroraweatherservice.bo

import ca.myasir.auroraweatherservice.dao.LocationServiceDao
import ca.myasir.auroraweatherservice.dao.WeatherProviderDao
import ca.myasir.auroraweatherservice.logger
import ca.myasir.auroraweatherservice.model.Coordinates
import ca.myasir.auroraweatherservice.model.LocationResult
import ca.myasir.auroraweatherservice.model.WeatherData
import ca.myasir.auroraweatherservice.model.WeatherProvider
import ca.myasir.auroraweatherservice.util.Latitude
import ca.myasir.auroraweatherservice.util.Longitude
import ca.myasir.auroraweatherservice.util.PlaceId
import org.springframework.stereotype.Service

/**
 * This BO will house all common logic related to getting locations, getting weather forecast for them, etc.
 * Though are features are simple right now, this BO doesn't do much. But in the future if we want do other things,
 * this BO will be responsible for handling the processing/business logic, which otherwise may be too convoluted for
 * the gRPC operation classes to handle.
 */
@Service
class LocationBo(

    private val locationServiceDao: LocationServiceDao,
    private val weatherProviderDao: WeatherProviderDao
) {

    /**
     * Call ALS to get a list of locations for the given search text
     */
    fun searchForLocations(searchText: String): List<LocationResult> {
        // Info logs can be quite messy in actual prod environment
        logger.debug { "Searching for location: $searchText" }

        return locationServiceDao.getLocations(searchText)
    }

    /**
     * Call ALS to get coordinates for the given placeId
     */
    fun getCoordinates(placeId: PlaceId): Coordinates {
        logger.info { "Getting coordinates for $placeId" }

        return locationServiceDao.getCoordinates(placeId)
    }

    /**
     * Call given weather provider's API to get a forecast for the given location
     */
    fun getWeatherForecast(longitude: Longitude, latitude: Latitude, provider: WeatherProvider): List<WeatherData> {
        return weatherProviderDao.getForecast(longitude, latitude, provider)
    }
}
