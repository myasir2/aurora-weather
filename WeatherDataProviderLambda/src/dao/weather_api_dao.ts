import {IWeatherDataProvider, NUM_FORECAST_DAYS} from "./index";
import {BaseWeatherData, WeatherInformation} from "../model/weather_information";
import {plainToClass, plainToInstance} from "class-transformer";
import {WeatherApiData} from "../model/provider/weather_api_data";

const BASE_URL = "https://api.weatherapi.com/v1/forecast.json"

/**
 * This DAO will call the WeatherAPI data provider. In the constructor, the API Key can be optionally supplied.
 * Else it will default to the one stored in the environment variables.
 */
export class WeatherApiDao implements IWeatherDataProvider {

    constructor(
        private readonly apiKey = process.env.WEATHER_API_KEY,
    ) {}

    public async getData(
        longitude: number,
        latitude: number,
        numForecastDays: number = NUM_FORECAST_DAYS
    ): Promise<WeatherInformation> {
        const query = `${longitude},${latitude}`
        const url = `${BASE_URL}?key=${this.apiKey}&q=${query}&days=${numForecastDays}`
        const response = await fetch(url)

        if(!response.ok) {
            console.error(`Error while querying WeatherAPI with url: ${url} => HTTP [${response.status}]`)

            throw new Error(`Error while querying WeatherAPI with url: ${url} => HTTP [${response.status}]`)
        }

        const data = await response.json();
        const weatherApiData = plainToInstance(WeatherApiData, data as Object)
        const weatherInformation = this.convertToWeatherInformation(weatherApiData)

        return Promise.resolve(weatherInformation)
    }

    private convertToWeatherInformation(data: WeatherApiData): WeatherInformation {
        const current = data.current
        const forecastDay = data.forecast.forecastday
        const currentDay = forecastDay[0].day
        const weatherInfoForecast: BaseWeatherData[] = []

        const currentWeather = new BaseWeatherData(
            forecastDay[0].date,
            data.current.temp_c,
            currentDay.mintemp_c,
            currentDay.maxtemp_c,
            current.wind_kph,
            current.wind_degree,
            current.humidity,
            current.dewpoint_c,
            current.uv,
            current.vis_km,
            current.condition.icon
        )
        weatherInfoForecast.push(currentWeather)

        // First element contains currentDay information, so we ignore it by splicing the array at index 0
        // This provider also doesn't give us wind direction and dewpoint for forecasted days at the "day" level
        const forecastedDays = forecastDay.slice(1).map((f): BaseWeatherData => {
            const day = f.day

            return new BaseWeatherData(
                f.date,
                day.avgtemp_c,
                day.mintemp_c,
                day.maxtemp_c,
                day.maxwind_kph,
                0,
                day.avghumidity,
                0,
                day.uv,
                day.avgvis_km,
                day.condition.icon
            )
        })
        weatherInfoForecast.push(...forecastedDays)

        return new WeatherInformation(weatherInfoForecast)
    }
}
