import "reflect-metadata";
import * as dotenv from "dotenv"
import {getSecret} from "@aws-lambda-powertools/parameters/secrets";
import {APIGatewayProxyHandler} from "aws-lambda";
import {WeatherApiDao} from "./dao/weather_api_dao";
import {GetWeatherDataResponse, WeatherData} from "@myasir/aurora-weather-data-provider"
import {WeatherInformation} from "./model/weather_information";

dotenv.config()

export const handler: APIGatewayProxyHandler = async (event) => {
    const apiKeysSecretName = process.env.API_KEYS_SECRET_NAME ?? ""
    const apiKeys = JSON.parse(await getSecret(apiKeysSecretName) ?? "")

    if (!apiKeys) {
        return {
            statusCode: 500,
            body: JSON.stringify({
                message: "API Keys missing",
            }),
        }
    }

    console.log(event)

    const weatherApiKey = apiKeys["WEATHER_API_KEY"]
    // const xWeatherApiKey = apiKeys["X_WEATHER_KEY"]
    // const xWeatherApiSecret = apiKeys["X_WEATHER_SECRET"]

    const weatherApiDao = new WeatherApiDao(weatherApiKey)
    // const xWeatherDao = new XWeatherDao(xWeatherApiKey, xWeatherApiSecret)

    return {
        statusCode: 200,
        body: JSON.stringify(convertToResponse(await weatherApiDao.getData(43.6532, -79.3832))),
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

