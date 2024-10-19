import {WeatherInformation} from "../model/weather_information";

/**
 * A value to get forecast for the number of days. All APIs, at the time of writing, support outputting forecast for
 * at least 3 days/
 */
export const NUM_FORECAST_DAYS = 3

/**
 * An interface to ensure all provider DAOs return the same structured data.
 */
export interface IWeatherDataProvider {

    /**
     * To get weather data for the given lon/lat location, and number of days for the forecast.
     */
    getData(longitude: number, latitude: number, numForecastDays: number): Promise<WeatherInformation>
}
