import {WeatherData} from "../../../proto/main_pb";
import PlaceIcon from "@mui/icons-material/Place";
import {CardContent, Typography} from "@mui/material";
import React from "react";
import AirIcon from '@mui/icons-material/Air';
import ExploreIcon from '@mui/icons-material/Explore';
import convert from "convert"

interface Props {
    weatherData: WeatherData;
}

const WindCard: React.FC<Props> = (props) => {
    const {weatherData} = props;
    const windSpeed = convert(weatherData.getWindspeed(), "km").to("km").toString()

    return (
        <CardContent
            className={"flex flex-col w-full rounded-xl justify-between backdrop-blur-md bg-gray-50/20 text-white"}>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    Wind Status
                </Typography>
                <AirIcon/>
            </div>
            <div className="text-center bg-transparent">
                <ExploreIcon style={{fontSize: 150}}/>
            </div>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    <Typography gutterBottom sx={{fontSize: 12}}>
                        Wind
                    </Typography>
                    {windSpeed}
                </Typography>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    <Typography gutterBottom sx={{fontSize: 12}}>
                        Direction
                    </Typography>
                    {weatherData.getWinddirectiondegree()} Degrees
                </Typography>
            </div>
        </CardContent>
    )
}

export default WindCard;
