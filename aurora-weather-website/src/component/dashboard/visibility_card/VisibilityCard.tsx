import {WeatherData} from "../../../proto/main_pb";
import {CardContent, Typography} from "@mui/material";
import React from "react";
import VisibilityIcon from '@mui/icons-material/Visibility';

interface Props {
    weatherData: WeatherData;
}

const VisibilityCard: React.FC<Props> = (props) => {
    const {weatherData} = props;

    return (
        <CardContent
            className={"flex flex-col w-full rounded-xl justify-between backdrop-blur-md bg-gray-50/20 text-white"}>
            <div className={"flex flex-row justify-between items-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    Visibility
                </Typography>
            </div>
            <div className="text-center bg-transparent">
                <VisibilityIcon style={{fontSize: 150}}/>
            </div>
            <div className={"flex flex-row justify-center"}>
                <Typography gutterBottom sx={{fontSize: 14}}>
                    {weatherData.getVisibility()}
                </Typography>
            </div>
        </CardContent>
    )
}

export default VisibilityCard;
