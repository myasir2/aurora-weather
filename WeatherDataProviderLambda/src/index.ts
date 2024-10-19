import 'reflect-metadata';
import * as dotenv from "dotenv"
import { GetWeatherDataRequest } from "@myasir/aurora-weather-data-provider"
import {WeatherApiDao} from "./dao/weather_api_dao";
import {XWeatherDao} from "./dao/x_weather_dao";

dotenv.config()

export const weatherApiDao = new WeatherApiDao()
export const xWeatherDao = new XWeatherDao()

const request: GetWeatherDataRequest = {
    longitude: 0.1,
    latitude: 0.1,
}

console.log(request)
