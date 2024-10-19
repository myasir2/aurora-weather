import * as express from "express";
import {Express, Request, Response} from "express";
import {WeatherApiDao} from "./src/dao/weather_api_dao";
import {XWeatherDao} from "./src/dao/x_weather_dao";

const weatherApiDao = new WeatherApiDao()
const xWeatherDao = new XWeatherDao()
const app: Express = express();
app.use(express.json());

app.post("/weather-api", async (req: Request, res: Response) => {
    const body = req.body;
    const longitude = body.longitude
    const latitude = body.latitude

    const information = await weatherApiDao.getData(longitude, latitude)

    res.contentType("application/json")
    res.send(information)
});

app.post("/x-weather", async (req: Request, res: Response) => {
    const body = req.body;
    const longitude = body.longitude
    const latitude = body.latitude

    const information = await xWeatherDao.getData(longitude, latitude)

    res.contentType("application/json")
    res.send(information)
});

app.listen(3000, "127.0.0.1", () => {
    console.log(`⚡️[server]: Server is running at http://localhost:3000`);
});
