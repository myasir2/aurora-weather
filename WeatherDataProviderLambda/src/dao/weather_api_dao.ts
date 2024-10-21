import {IWeatherDataProvider} from "./index";
import {BaseWeatherData, WeatherInformation} from "../model/weather_information";
import {plainToInstance} from "class-transformer";
import {WeatherApiData} from "../model/provider/weather_api_data";

const BASE_URL = "https://api.weatherapi.com/v1/forecast.json"

/**
 * This DAO will call the WeatherAPI data provider. In the constructor, the API Key can be optionally supplied.
 * Else it will default to the one stored in the environment variables.
 */
export class WeatherApiDao implements IWeatherDataProvider {

    constructor(
        private readonly apiKey = process.env.WEATHER_API_KEY
    ) {}

    public async getData(
        longitude: number,
        latitude: number,
        numForecastDays: number
    ): Promise<WeatherInformation> {
        const query = `${longitude},${latitude}`
        const url = `${BASE_URL}?q=${query}&days=${numForecastDays}`

        console.log(`Fetching WeatherAPI with url: ${url}`)

        const response = await fetch(url + `&key=${this.apiKey}`)

        if(!response.ok) {
            console.error(`Error while querying WeatherAPI with url: ${url} => HTTP [${response.status}]`)

            throw new Error(`Error while querying WeatherAPI with url: ${url} => HTTP [${response.status}]`)
        }

        const data = await response.json();

        console.log(`Fetched data: ${JSON.stringify(data)}`)

        const weatherApiData = plainToInstance(WeatherApiData, data as object)
        const weatherInformation = this.convertToWeatherInformation(weatherApiData)

        return Promise.resolve(weatherInformation)
    }

    private convertToWeatherInformation(data: WeatherApiData): WeatherInformation {
        const current = data.current
        const forecastDay = data.forecast.forecastday
        const currentDay = forecastDay[0].day
        const weatherInfoForecast: BaseWeatherData[] = []

        const currentWeather = new BaseWeatherData(
            new Date(forecastDay[0].date_epoch * 1000).toISOString(),
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
                new Date(f.date_epoch * 1000).toISOString(),
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
