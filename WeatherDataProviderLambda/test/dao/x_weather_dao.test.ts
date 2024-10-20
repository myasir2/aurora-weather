import "reflect-metadata";
import {XWeatherDao} from "../../src/dao/x_weather_dao";
import {enableFetchMocks} from "jest-fetch-mock";
import {
    TEST_API_KEY,
    TEST_API_SECRET,
    TEST_CURRENT_DATE,
    TEST_DEWPOINT,
    TEST_FORECAST_DAY1_DATE,
    TEST_HUMIDITY,
    TEST_LATITUDE,
    TEST_LONGITUDE,
    TEST_MAX_TEMP,
    TEST_MIN_TEMP,
    TEST_NUM_FORECAST_DAYS,
    TEST_TEMP,
    TEST_UV,
    TEST_VISIBILITY,
    TEST_WEATHER_ICON_URL,
    TEST_WEATHER_INFO,
    TEST_WIND_DEGREE,
    TEST_WIND_SPEED
} from "../util/test_defaults";

const TEST_MOCKED_RESPONSE = {
    response: [
        {
            periods: [
                {
                    dateTimeISO: TEST_CURRENT_DATE,
                    maxTempC: TEST_MAX_TEMP,
                    minTempC: TEST_MIN_TEMP,
                    tempC: TEST_TEMP,
                    dewpointC: TEST_DEWPOINT,
                    humidity: TEST_HUMIDITY,
                    windSpeedKPH: TEST_WIND_SPEED,
                    windDirDEG: TEST_WIND_DEGREE,
                    uvi: TEST_UV,
                    visibilityKM: TEST_VISIBILITY,
                    icon: TEST_WEATHER_ICON_URL,
                },
                {
                    dateTimeISO: TEST_FORECAST_DAY1_DATE,
                    maxTempC: TEST_MAX_TEMP,
                    minTempC: TEST_MIN_TEMP,
                    tempC: TEST_TEMP,
                    dewpointC: 0,
                    humidity: TEST_HUMIDITY,
                    windSpeedKPH: TEST_WIND_SPEED,
                    windDirDEG: 0,
                    uvi: TEST_UV,
                    visibilityKM: TEST_VISIBILITY,
                    icon: TEST_WEATHER_ICON_URL,
                }
            ],
        }
    ],
}

describe("XWeatherDao", () => {
    let dao: XWeatherDao;

    beforeAll(() => {
        enableFetchMocks()
    })

    beforeEach(() => {
        fetchMock.resetMocks()

        dao = new XWeatherDao(TEST_API_KEY, TEST_API_SECRET)
    })

    it("should receive a successful XWeather response and parse it correctly", async () => {
        fetchMock.mockReturnValue(Promise.resolve(new Response(JSON.stringify(TEST_MOCKED_RESPONSE))))

        const actualWeatherInfo = await dao.getData(TEST_LONGITUDE, TEST_LATITUDE, TEST_NUM_FORECAST_DAYS)

        expect(actualWeatherInfo).toEqual(TEST_WEATHER_INFO)
    });

    it("should throw an Error if XWeather responds with a non-200 status code", async () => {
        fetchMock.mockReturnValue(Promise.reject(new Error("Something broke")))

        await expect(dao.getData(TEST_LONGITUDE, TEST_LATITUDE, TEST_NUM_FORECAST_DAYS)).rejects.toThrow()
    });
});
