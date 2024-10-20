import {IWeatherDataProvider} from "./index";
import {BaseWeatherData, WeatherInformation} from "../model/weather_information";
import {plainToInstance} from "class-transformer";
import {XWeatherData} from "../model/provider/x_weather_data";

const BASE_URL = "https://data.api.xweather.com/forecasts"

export class XWeatherDao implements IWeatherDataProvider {

    constructor(
        private readonly apiKey = process.env.X_WEATHER_KEY,
        private readonly apiSecret = process.env.X_WEATHER_SECRET
    ) {
    }

    public async getData(
        longitude: number,
        latitude: number,
        numForecastDays: number
    ): Promise<WeatherInformation> {
        // This API doesn't count today as a day. For example, for 3 days, it will return today and plus 3 days (4 days).
        // Therefore, we subtract 1 from forecast days to ensure results are consistent.
        const numDays = numForecastDays - 1
        const location = `${longitude},${latitude}`
        const url = `${BASE_URL}/${location}?from=${new Date().toISOString()}&to=+${numDays}days&filter=24hr`

        console.log(`Fetching XWeather url: ${url}`)

        const response = await fetch(url + `&client_id=${this.apiKey}&client_secret=${this.apiSecret}`)

        if(!response.ok) {
            console.error(`Error while querying XWeather with url: ${url} => HTTP [${response.status}]`)

            throw new Error(`Error while querying XWeather with url: ${url} => HTTP [${response.status}]`)
        }

        const data = await response.json();

        console.log(`Fetched data: ${JSON.stringify(data)}`)

        const xWeatherData = plainToInstance(XWeatherData, data as object)
        const weatherInformation = this.convertToWeatherInformation(xWeatherData)

        return Promise.resolve(weatherInformation)
    }

    private convertToWeatherInformation(data: XWeatherData): WeatherInformation {
        const forecast = data.response[0].periods.map(p =>
            new BaseWeatherData(
                p.dateTimeISO,
                p.tempC,
                p.minTempC,
                p.maxTempC,
                p.windSpeedKPH,
                p.windDirDEG,
                p.humidity,
                p.dewpointC,
                p.uvi,
                p.visibilityKM,
                p.icon
            )
        )

        return new WeatherInformation(forecast)
    }
}
