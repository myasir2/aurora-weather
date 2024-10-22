import {Autocomplete, CircularProgress, InputAdornment, TextField, Typography} from "@mui/material";
import React, {ChangeEvent, useEffect} from "react";
import LocationCard from "./city_card/CityCard";
import BackendApiServiceDao from "../dao/BackendApiServiceDao";
import LocalStorageDao, {FavouriteLocation} from "../dao/LocalStorageDao";
import SearchIcon from '@mui/icons-material/Search';
import {Coordinates, LocationResult} from "../../proto/main_pb";

const api = BackendApiServiceDao.getInstance()

export interface AutoCompleteOption {
    readonly id: string;
    readonly label: string;
}

interface Props {
    readonly onLocationSelected: (location: LocationResult) => void;
    readonly favouriteLocations: FavouriteLocation[]
}

const SideMenu: React.FC<Props> = (props) => {
    const {favouriteLocations} = props;
    const [loading, setLoading] = React.useState(false);
    const [locationOptions, setLocationOptions] = React.useState<AutoCompleteOption[]>([]);
    const locationCards = favouriteLocations.map(l =>
        <LocationCard favouriteLocation={l} onClick={() => {
            const locationResult = new LocationResult()
            locationResult.setPlaceid(l.placeId)
            locationResult.setLocationname(l.locationName)

            props.onLocationSelected(locationResult)
        }}/>
    )

    const convertLocationHitToOption = (location: LocationResult): AutoCompleteOption => {
        return {
            id: location.getPlaceid(),
            label: location.getLocationname()
        }
    }

    // For every search input, fetch locations and show to user
    const handleSearchInput = async (e: ChangeEvent<HTMLInputElement>) => {
        const searchText = e.target.value;

        if (searchText.trim().length === 0) {
            return;
        }

        setLoading(true);

        const locations = await api.searchLocations(searchText)
        const options = locations.map(convertLocationHitToOption)

        setLoading(false)
        setLocationOptions(options)
    }

    // After a location is selected, return the selected location to the parent component so it can fetch the coordinates,
    // and get weather forecast
    const handleSearchOptionSelected = (option: AutoCompleteOption) => {
        if (!option || option.id.trim().length === 0) {
            return
        }

        const placeId = option.id
        const loctionName = option.label

        const location = new LocationResult()
        location.setPlaceid(placeId)
        location.setLocationname(loctionName)

        props.onLocationSelected(location)
    }

    return (
        <div className="flex flex-col h-screen justify-evenly">
            <Typography className={"mb-5"} gutterBottom sx={{fontSize: 20}}>
                AuroraWeather
            </Typography>
            <Autocomplete
                disablePortal
                filterOptions={(x) => x}
                options={locationOptions}
                freeSolo={locationOptions.length === 0}
                noOptionsText={"No cities found"}
                onChange={(e, value) =>
                    handleSearchOptionSelected(value as AutoCompleteOption)
                }
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label="Search for a city"
                        variant={"outlined"}
                        onChange={handleSearchInput}
                        slotProps={{
                            input: {
                                ...params.InputProps,
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon/>
                                    </InputAdornment>
                                ),
                                endAdornment: (
                                    <React.Fragment>
                                        {loading ? <CircularProgress color="inherit" size={20}/> : null}
                                        {params.InputProps.endAdornment}
                                    </React.Fragment>
                                ),
                            },
                        }}
                    />
                )}
            />
            <div className={"flex flex-col grow space-y-2 mt-1.5"}>
                {locationCards}
            </div>
        </div>
    )
}

export default SideMenu;
