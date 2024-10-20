import "reflect-metadata";
import * as dotenv from "dotenv"
import {getSecret} from "@aws-lambda-powertools/parameters/secrets";
import {APIGatewayProxyHandler} from "aws-lambda";
import {WeatherApiDao} from "./dao/weather_api_dao";
import {GetWeatherDataRequest, GetWeatherDataResponse, WeatherData} from "@myasir/aurora-weather-data-provider"
import {WeatherInformation} from "./model/weather_information";
import {XWeatherDao} from "./dao/x_weather_dao";

dotenv.config()

const WEATHER_API_PATH = "/weather-api"
const X_WEATHER_API_PATH = "/x-weather"

export const handler: APIGatewayProxyHandler = async (event) => {
    const apiKeysSecretName = process.env.API_KEYS_SECRET_NAME ?? ""
    const apiKeys = JSON.parse(await getSecret(apiKeysSecretName) ?? "")
    const path = event.path
    const request = JSON.parse(event.body ?? "") as GetWeatherDataRequest
    const {longitude, latitude, numForecastDays,} = request

    console.log(`Request: ${JSON.stringify(request)}`)

    if (!longitude || !latitude || !numForecastDays) {
        throw new Error("Missing longitude, latitude or numForecastDays")
    }

    if (!apiKeys) {
        return {
            statusCode: 500,
            body: JSON.stringify({
                message: "API Keys missing",
            }),
        }
    }

    const weatherApiKey = apiKeys["WEATHER_API_KEY"]
    const xWeatherApiKey = apiKeys["X_WEATHER_KEY"]
    const xWeatherApiSecret = apiKeys["X_WEATHER_SECRET"]

    const weatherApiDao = new WeatherApiDao(weatherApiKey)
    const xWeatherDao = new XWeatherDao(xWeatherApiKey, xWeatherApiSecret)

    let weatherInformation: WeatherInformation

    switch (path) {
    case WEATHER_API_PATH:
        weatherInformation = await weatherApiDao.getData(longitude, latitude, numForecastDays)
        break
    case X_WEATHER_API_PATH:
        weatherInformation = await xWeatherDao.getData(longitude, latitude, numForecastDays)
        break
    default:
        throw new Error(`Path ${path} not configured for any providers`)
    }

    return {
        statusCode: 200,
        body: JSON.stringify(convertToResponse(weatherInformation)),
    }
}

/**
 * Convert to API gateway output model.
 */
const convertToResponse = (info: WeatherInformation): GetWeatherDataResponse => {
    const data = info.forecast.map((i): WeatherData => {
        return {
            date: i.date,
            temp: i.temp,
            minTemp: i.minTemp,
            maxTemp: i.maxTemp,
            windSpeed: i.windSpeed,
            windDirectionDegree: i.windDirectionDegree,
            humidity: i.humidity,
            dewpoint: i.dewpoint,
            uv: i.uv,
            visibility: i.visibility,
            weatherIconUrl: i.weatherIconUrl,
        }
    })

    return {
        forecast: data,
    }
}

