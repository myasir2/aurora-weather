import {Card, CardActionArea, CardContent, Typography} from "@mui/material";
import React from "react";
import PlaceIcon from '@mui/icons-material/Place';
import {FavouriteLocation} from "../../dao/LocalStorageDao";

interface Props {
    readonly favouriteLocation: FavouriteLocation
    readonly onClick: () => void
}

const CityCard: React.FC<Props> = (props: Props) => {
    const {favouriteLocation} = props;

    return (
        <Card>
            <CardActionArea onClick={props.onClick}>
                <CardContent className={"flex flex-row w-full bg-white rounded-lg justify-between"}>
                    <div className={"flex flex-row justify-evenly items-center"}>
                        <PlaceIcon/>
                        <Typography gutterBottom sx={{color: 'text.secondary', fontSize: 14}}>
                            {favouriteLocation.locationName}
                        </Typography>
                    </div>
                </CardContent>
            </CardActionArea>
        </Card>
    )
}

export default CityCard
