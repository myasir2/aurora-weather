import {WeatherData} from "../../../proto/main_pb";
import {CardContent, Typography} from "@mui/material";
import React from "react";
import WbSunnyIcon from '@mui/icons-material/WbSunny';

interface Props {
    weatherData: WeatherData;
}

const UvCard: React.FC<Props> = (props) => {
    const {weatherData} = props;

    return (
        <CardContent
            className={"flex flex-col w-full rounded-xl justify-between backdrop-blur-md bg-gray-50/20 text-white"}>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    UV Index
                </Typography>
            </div>
            <div className="text-center bg-transparent">
                <WbSunnyIcon style={{fontSize: 150}}/>
            </div>
            <div className={"flex flex-row justify-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    {weatherData.getUv()} UV
                </Typography>
            </div>
        </CardContent>
    )
}

export default UvCard;
