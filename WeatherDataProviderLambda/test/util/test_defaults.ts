import {BaseWeatherData, WeatherInformation} from "../../src/model/weather_information";

export const TEST_LONGITUDE = 43.6532
export const TEST_LATITUDE = -79.3832
export const TEST_API_KEY = "apikey"
export const TEST_API_SECRET = "secret"
export const TEST_TEMP = 12.0
export const TEST_MIN_TEMP = 10.0
export const TEST_MAX_TEMP = 15.0
export const TEST_WIND_SPEED = 25
export const TEST_WIND_DEGREE = 250
export const TEST_HUMIDITY = 86
export const TEST_DEWPOINT = 9
export const TEST_UV = 5
export const TEST_VISIBILITY = 10
export const TEST_WEATHER_ICON_URL = "icon.png"
export const TEST_CURRENT_DATE = new Date("2024-01-01").toISOString()
export const TEST_FORECAST_DAY1_DATE = new Date("2024-01-02").toISOString()
export const TEST_CURRENT_WEATHER = new BaseWeatherData(
    TEST_CURRENT_DATE,
    TEST_TEMP,
    TEST_MIN_TEMP,
    TEST_MAX_TEMP,
    TEST_WIND_SPEED,
    TEST_WIND_DEGREE,
    TEST_HUMIDITY,
    TEST_DEWPOINT,
    TEST_UV,
    TEST_VISIBILITY,
    TEST_WEATHER_ICON_URL
)
export const TEST_FORECAST_DAY1_WEATHER = new BaseWeatherData(
    TEST_FORECAST_DAY1_DATE,
    TEST_TEMP,
    TEST_MIN_TEMP,
    TEST_MAX_TEMP,
    TEST_WIND_SPEED, 0, TEST_HUMIDITY, 0, TEST_UV, TEST_VISIBILITY,
    TEST_WEATHER_ICON_URL
)
export const TEST_WEATHER_INFO = new WeatherInformation(
    [TEST_CURRENT_WEATHER, TEST_FORECAST_DAY1_WEATHER]
)
