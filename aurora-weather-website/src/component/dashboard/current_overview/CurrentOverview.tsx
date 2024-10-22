import {WeatherData} from "../../../proto/main_pb";
import {CardContent, Typography} from "@mui/material";
import React from "react";
import VisibilityIcon from '@mui/icons-material/Visibility';
import {format} from 'date-fns';
import CloudIcon from '@mui/icons-material/Cloud';

interface Props {
    weatherData: WeatherData;
}

const CurrentOverview: React.FC<Props> = (props) => {
    const {weatherData} = props;
    const date = new Date()
    const formattedTime = format(date, 'h:mm a');
    const formattedDate = format(date, 'EEEE, d MMMM yyyy');

    return (
        <div className={"text-white text-center"}>
            <Typography gutterBottom sx={{fontSize: 14}}>
                {formattedTime}
                <br/>
                {formattedDate}
            </Typography>
            <CloudIcon style={{fontSize: 100}}/>
            <Typography gutterBottom sx={{fontSize: 100}}>
                {weatherData.getTemp()}
            </Typography>
            <div className="flex flex-row w-full justify-between">
                <Typography gutterBottom sx={{fontSize: 25}}>
                    H:{weatherData.getMaxtemp()}
                </Typography>
                <Typography gutterBottom sx={{fontSize: 25}}>
                    L:{weatherData.getMintemp()}
                </Typography>
            </div>
        </div>
    )
}

export default CurrentOverview;
