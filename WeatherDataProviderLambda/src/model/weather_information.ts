/**
 * A class to represent weather data. All information should be stored in metric system format.
 * E.g. Celsius instead of Fahrenheit
 */
export class BaseWeatherData {

    date: string
    temp: number
    minTemp: number
    maxTemp: number
    windSpeed: number
    windDirectionDegree: number
    humidity: number
    dewpoint: number
    uv: number
    visibility: number
    weatherIconUrl: string

    constructor(date: string, temp: number, minTemp: number, maxTemp: number, windSpeed: number, windDirectionDegree: number, humidity: number, dewpoint: number, uv: number, visibility: number, weatherIconUrl: string) {
        this.date = date;
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.windSpeed = windSpeed;
        this.windDirectionDegree = windDirectionDegree;
        this.humidity = humidity;
        this.dewpoint = dewpoint;
        this.uv = uv;
        this.visibility = visibility;
        this.weatherIconUrl = weatherIconUrl;
    }
}

export class WeatherInformation {

    forecast: BaseWeatherData[]

    constructor(dailyForecast: BaseWeatherData[]) {
        this.forecast = dailyForecast;
    }
}
