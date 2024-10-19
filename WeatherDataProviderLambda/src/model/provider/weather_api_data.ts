import {Type} from "class-transformer";

/**
 * This is a wrapper around the response of the WeatherAPI provider. Full details of the API's response can be found
 * under "Forecast API": https://www.weatherapi.com/docs/
 */
export class WeatherApiData {

    current: WeatherApiCurrent
    forecast: WeatherApiForecast

    constructor(current: WeatherApiCurrent, forecast: WeatherApiForecast) {
        this.current = current;
        this.forecast = forecast;
    }
}

class WeatherApiCurrent {

    temp_c: number
    wind_kph: number
    wind_degree: number
    humidity: number
    dewpoint_c: number
    uv: number
    vis_km: number
    condition: Condition

    constructor(
        temp_c: number,
        wind_kph: number,
        wind_degree: number,
        humidity: number,
        dewpoint_c: number,
        uv: number,
        vis_km: number,
        condition: Condition
    ) {
        this.temp_c = temp_c;
        this.wind_kph = wind_kph;
        this.wind_degree = wind_degree;
        this.humidity = humidity;
        this.dewpoint_c = dewpoint_c;
        this.uv = uv;
        this.vis_km = vis_km;
        this.condition = condition;
    }
}

class WeatherApiForecast {

    @Type(() => WeatherApiForecastDay)
        forecastday: WeatherApiForecastDay[]

    constructor(forecastday: WeatherApiForecastDay[]) {
        this.forecastday = forecastday;
    }
}

class WeatherApiForecastDay {

    date: string
    day: Day

    constructor(date: string, day: Day) {
        this.date = date;
        this.day = day;
    }
}

export class Day {

    mintemp_c: number
    maxtemp_c: number
    avgtemp_c: number
    maxwind_kph: number
    avgvis_km: number
    avghumidity: number
    uv: number
    condition: Condition

    constructor(mintemp_c: number, maxtemp_c: number, avgtemp_c: number, maxwind_kph: number, avgvis_km: number, avghumidity: number, uv: number, condition: Condition) {
        this.mintemp_c = mintemp_c;
        this.maxtemp_c = maxtemp_c;
        this.avgtemp_c = avgtemp_c;
        this.maxwind_kph = maxwind_kph;
        this.avgvis_km = avgvis_km;
        this.avghumidity = avghumidity;
        this.uv = uv;
        this.condition = condition;
    }
}

class Condition {

    icon: string

    constructor(icon: string) {
        this.icon = icon;
    }
}
