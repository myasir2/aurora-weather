import {WeatherData} from "../../../proto/main_pb";
import PlaceIcon from "@mui/icons-material/Place";
import {CardContent, Typography} from "@mui/material";
import React from "react";
import AirIcon from '@mui/icons-material/Air';
import WaterDropIcon from '@mui/icons-material/WaterDrop';

interface Props {
    weatherData: WeatherData;
}

const HumidityCard: React.FC<Props> = (props) => {
    const {weatherData} = props;

    return (
        <CardContent
            className={"flex flex-col w-full rounded-xl justify-between backdrop-blur-md bg-gray-50/20 text-white"}>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    Humidity
                </Typography>
            </div>
            <div className="text-center bg-transparent">
                <WaterDropIcon style={{fontSize: 150}}/>
            </div>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    <Typography gutterBottom sx={{fontSize: 12}}>
                        Humidity
                    </Typography>
                    {weatherData.getHumidity()}%
                </Typography>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    <Typography gutterBottom sx={{fontSize: 12}}>
                        Dewpoint
                    </Typography>
                    {weatherData.getDewpoint()}
                </Typography>
            </div>
        </CardContent>
    )
}

export default HumidityCard;
