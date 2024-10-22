import {Alert, CircularProgress, IconButton, Snackbar, Typography} from "@mui/material";
import {LocationResult, WeatherData, WeatherProvider} from "../../proto/main_pb";
import PlaceIcon from "@mui/icons-material/Place";
import WindCard from "./wind_card/WindCard";
import HumidityCard from "./humidity_card/HumidityCard";
import UvCard from "./uv_card/UvCard";
import VisibilityCard from "./visibility_card/VisibilityCard";
import CurrentOverview from "./current_overview/CurrentOverview";
import React, {useEffect} from "react";
import SettingsDialog from "./settings/SettingsDialog";
import BackendApiServiceDao from "../dao/BackendApiServiceDao";
import LocalStorageDao, {FavouriteLocation} from "../dao/LocalStorageDao";
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import ForecastCard from "./forecast/ForecastCard";

interface Props {
    readonly location: LocationResult | null
    readonly isLocationAlreadyFavourited: boolean
    readonly onAddLocationFavourite: (location: FavouriteLocation) => void
    readonly onRemoveLocationFavourite: (location: FavouriteLocation) => void
}

const api = BackendApiServiceDao.getInstance()
const localStorageDao = LocalStorageDao.getInstance()

const Dashboard: React.FC<Props> = (props) => {
    const {location, isLocationAlreadyFavourited, onAddLocationFavourite, onRemoveLocationFavourite} = props;
    const [loading, setLoading] = React.useState(false);
    const [currentWeatherData, setCurrentWeatherData] = React.useState<WeatherData>(new WeatherData());
    const [forecastData, setForecastData] = React.useState<WeatherData[]>([]);
    const [favouriteLocation, setFavouriteLocation] = React.useState<FavouriteLocation>()
    const [error, setError] = React.useState<string | null>(null);

    const addFavouriteLocation = () => {
        if (!favouriteLocation) {
            return
        }

        onAddLocationFavourite(favouriteLocation)
    }

    const removeFavouriteLocation = () => {
        if (!favouriteLocation) {
            return
        }

        onRemoveLocationFavourite(favouriteLocation)
    }

    const createForecastCards = (): JSX.Element[] => {
        return forecastData.map(f => <ForecastCard weatherData={f}/>)
    }

    useEffect(() => {
        if (!location) {
            return
        }

        setLoading(true)

        const placeId = location.getPlaceid()
        const weatherProvider = WeatherProvider[localStorageDao.getProvider() as keyof typeof WeatherProvider]

        api.getCoordinates(placeId)
            .then(coordinates => {
                api.getForecast(coordinates, weatherProvider)
                    .then(forecast => {
                        setCurrentWeatherData(forecast[0])
                        setForecastData(forecast)
                        setFavouriteLocation({
                            placeId: location.getPlaceid(),
                            locationName: location.getLocationname(),
                            longitude: coordinates.getLongitude(),
                            latitude: coordinates.getLatitude(),
                        })
                        setLoading(false)
                    })
                    .catch(error => {
                        setError(error.message)
                    })
            })
            .catch(error => {
                setError(error.message)
            })
    }, [location]);

    if (!location) {
        return (
            <div className="flex text-center text-white justify-center">
                <h2>Please select a location to view its forecast</h2>
            </div>
        )
    }

    return (
        <div className={"flex flex-col m-20 items-start"}>
            <Snackbar open={error !== null} autoHideDuration={6000} onClose={() => {
                setError(null)
            }}>
                <Alert
                    severity="error"
                    variant="filled"
                    sx={{width: '100%'}}
                >
                    {error}
                </Alert>
            </Snackbar>
            <div className={"flex flex-row justify-between items-center text-white w-full"}>
                <div className="flex flex-col">
                    <Typography gutterBottom>
                        <PlaceIcon className={"mr-5"}/> {location.getLocationname()}
                    </Typography>
                </div>
                <div className="flex flex-col">
                    <div className="flex flex-row">
                        {
                            isLocationAlreadyFavourited ? (
                                <IconButton onClick={removeFavouriteLocation}>
                                    <FavoriteIcon/>
                                </IconButton>
                            ) : (
                                <IconButton onClick={addFavouriteLocation}>
                                    <FavoriteBorderIcon/>
                                </IconButton>
                            )
                        }
                        <IconButton>
                            <SettingsDialog/>
                        </IconButton>
                    </div>
                </div>
            </div>
            <div className="flex flex-row flex-wrap-reverse mt-20 justify-center sm:justify-start w-full">
                {
                    loading ? (
                        <CircularProgress/>
                    ) : (
                        <>
                            <div className={"grid grid-cols-1 gap-5 w-full md:w-auto sm:grid-cols-2 justify-between"}>
                                <WindCard weatherData={currentWeatherData}/>
                                <HumidityCard weatherData={currentWeatherData}/>
                                <UvCard weatherData={currentWeatherData}/>
                                <VisibilityCard weatherData={currentWeatherData}/>
                            </div>
                            <div className="flex flex-col sm:ml-20">
                                <CurrentOverview weatherData={currentWeatherData}/>
                            </div>
                        </>
                    )
                }
            </div>
            <div className="flex flex-row mt-20 justify-center sm:justify-start w-full">
                {
                    loading ? (
                        <CircularProgress/>
                    ) : (
                        <div className={"grid grid-cols-1 gap-5 w-full md:w-auto sm:grid-cols-3"}>
                            {createForecastCards()}
                        </div>
                    )
                }
            </div>
        </div>
    )
}

export default Dashboard
