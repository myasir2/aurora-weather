import {WeatherInformation} from "../model/weather_information";

/**
 * An interface to ensure all provider DAOs return the same structured data.
 */
export interface IWeatherDataProvider {

    /**
     * To get weather data for the given lon/lat location, and number of days for the forecast.
     */
    getData(longitude: number, latitude: number, numForecastDays: number): Promise<WeatherInformation>
}
