import {
    WeatherData
} from "../../../../../AuroraWeatherServiceInterface/build/generated/source/proto/main/grpc-web/main_pb";
import React from "react";
import {CardContent, Typography} from "@mui/material";
import WaterDropIcon from "@mui/icons-material/WaterDrop";
import {format} from "date-fns";

interface Props {
    weatherData: WeatherData;
}

const ForecastCard: React.FC<Props> = (props) => {
    const {weatherData} = props;
    const date = new Date(weatherData.getDate())
    const dayName = format(date, 'EEEE');

    return (
        <CardContent
            className={"flex flex-col w-full rounded-xl justify-between backdrop-blur-md bg-gray-50/20 text-white"}>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    {dayName}
                </Typography>
            </div>
            <div className="text-center bg-transparent">
                <Typography gutterBottom sx={{fontSize: 25}}>
                    {weatherData.getTemp()}
                </Typography>
            </div>
        </CardContent>
    )
}

export default ForecastCard;
