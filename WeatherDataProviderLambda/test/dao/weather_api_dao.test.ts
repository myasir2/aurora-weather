import 'reflect-metadata';
import {WeatherApiDao} from "../../src/dao/weather_api_dao";
import {enableFetchMocks} from "jest-fetch-mock";
import {
    TEST_API_KEY,
    TEST_CURRENT_DATE,
    TEST_DEWPOINT, TEST_FORECAST_DAY1_DATE,
    TEST_HUMIDITY, TEST_LATITUDE, TEST_LONGITUDE, TEST_MAX_TEMP, TEST_MIN_TEMP,
    TEST_TEMP,
    TEST_UV, TEST_VISIBILITY, TEST_WEATHER_ICON_URL, TEST_WEATHER_INFO,
    TEST_WIND_DEGREE,
    TEST_WIND_SPEED
} from "../util/test_defaults";

const TEST_MOCKED_RESPONSE = {
    current: {
        temp_c: TEST_TEMP,
        wind_kph: TEST_WIND_SPEED,
        wind_degree: TEST_WIND_DEGREE,
        humidity: TEST_HUMIDITY,
        dewpoint_c: TEST_DEWPOINT,
        uv: TEST_UV,
        vis_km: TEST_VISIBILITY,
        condition: {
            icon: TEST_WEATHER_ICON_URL
        }
    },
    forecast: {
        forecastday: [
            {
                date: TEST_CURRENT_DATE,
                day: {
                    mintemp_c: TEST_MIN_TEMP,
                    maxtemp_c: TEST_MAX_TEMP,
                    avgtemp_c: TEST_TEMP,
                    maxwind_kph: TEST_WIND_SPEED,
                    avgvis_km: TEST_VISIBILITY,
                    avghumidity: TEST_HUMIDITY,
                    uv: TEST_UV,
                    condition: {
                        icon: TEST_WEATHER_ICON_URL
                    }
                }
            },
            {
                date: TEST_FORECAST_DAY1_DATE,
                day: {
                    mintemp_c: TEST_MIN_TEMP,
                    maxtemp_c: TEST_MAX_TEMP,
                    avgtemp_c: TEST_TEMP,
                    maxwind_kph: TEST_WIND_SPEED,
                    avgvis_km: TEST_VISIBILITY,
                    avghumidity: TEST_HUMIDITY,
                    uv: TEST_UV,
                    condition: {
                        icon: TEST_WEATHER_ICON_URL
                    }
                }
            }
        ]
    }
}

describe('WeatherApiDao', () => {
    let dao: WeatherApiDao;

    beforeAll(() => {
        enableFetchMocks()
    })

    beforeEach(() => {
        fetchMock.resetMocks()

        dao = new WeatherApiDao(TEST_API_KEY)
    })

    it('should receive a successful WeatherAPI response and parse it correctly', async () => {
        fetchMock.mockReturnValue(Promise.resolve(new Response(JSON.stringify(TEST_MOCKED_RESPONSE))))

        const actualWeatherInfo = await dao.getData(TEST_LONGITUDE, TEST_LATITUDE)

        expect(actualWeatherInfo).toEqual(TEST_WEATHER_INFO)
    });

    it('should throw an Error if WeatherAPI responds with a non-200 status code', async () => {
        fetchMock.mockReturnValue(Promise.reject(new Error("Something broke")))

        await expect(dao.getData(TEST_LONGITUDE, TEST_LATITUDE)).rejects.toThrow()
    });
})
