import "reflect-metadata";
import * as dotenv from "dotenv"
import {getSecret} from "@aws-lambda-powertools/parameters/secrets";
import {APIGatewayProxyHandler} from "aws-lambda";
import {XWeatherDao} from "./dao/x_weather_dao";
import {WeatherApiDao} from "./dao/weather_api_dao";

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
    const xWeatherApiKey = apiKeys["X_WEATHER_KEY"]
    const xWeatherApiSecret = apiKeys["X_WEATHER_SECRET"]

    const weatherApiDao = new WeatherApiDao(weatherApiKey)
    const xWeatherDao = new XWeatherDao(xWeatherApiKey, xWeatherApiSecret)

    return {
        statusCode: 200,
        body: JSON.stringify({
            weather_api: await weatherApiDao.getData(43.6532, -79.3832),
            x_weather_api: await xWeatherDao.getData(43.6532, -79.3832),
        }),
    }
}
