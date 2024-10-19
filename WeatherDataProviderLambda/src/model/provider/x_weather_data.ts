import {Type} from "class-transformer";

export class XWeatherData {

    @Type(() => XWeatherDataResponse)
        response: XWeatherDataResponse[]

    constructor(response: XWeatherDataResponse[]) {
        this.response = response;
    }
}

class XWeatherDataResponse {

    @Type(() => XWeatherDataPeriod)
        periods: XWeatherDataPeriod[]

    constructor(periods: XWeatherDataPeriod[]) {
        this.periods = periods;
    }
}

class XWeatherDataPeriod {

    dateTimeISO: string
    maxTempC: number
    minTempC: number
    tempC: number
    dewpointC: number
    humidity: number
    windSpeedKPH: number
    windDirDEG: number
    uvi: number
    visibilityKM: number
    icon: string

    constructor(dateTimeISO: string, maxTempC: number, minTempC: number, tempC: number, dewpointC: number, humidity: number, windSpeedKPH: number, windDirDEG: number, uvi: number, visibilityKM: number, icon: string) {
        this.dateTimeISO = dateTimeISO;
        this.maxTempC = maxTempC;
        this.minTempC = minTempC;
        this.tempC = tempC;
        this.dewpointC = dewpointC;
        this.humidity = humidity;
        this.windSpeedKPH = windSpeedKPH;
        this.windDirDEG = windDirDEG;
        this.uvi = uvi;
        this.visibilityKM = visibilityKM;
        this.icon = icon;
    }
}
