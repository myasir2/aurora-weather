import React, {useEffect, useState} from 'react';
import logo from '../logo.svg';
import './App.css';
import {AuroraWeatherServiceClient} from "../proto/main_grpc_web_pb"
import {Empty} from "google-protobuf/google/protobuf/empty_pb";
import SideMenu from "./side_menu/SideMenu";
import Dashboard from "./dashboard/Dashboard";
import {AppBar, Box, createTheme, Drawer, IconButton, ThemeProvider, Toolbar, Typography} from "@mui/material";
import {Coordinates, LocationResult, WeatherData} from "../proto/main_pb";
import MenuIcon from '@mui/icons-material/Menu';
import LocalStorageDao, {FavouriteLocation} from "./dao/LocalStorageDao";

const theme = createTheme({
    palette: {
        mode: 'light',
        // Set the mode to light
    },
    components: {
        // You can add component overrides here
        MuiButton: {
            styleOverrides: {
                root: {
                    // Example: Change default button styles
                    borderRadius: '8px',
                },
            },
        },
        MuiAutocomplete: {
            styleOverrides: {
                popper: {
                    marginTop: '8px', // Adjust this value to increase/decrease spacing
                },
                inputRoot: {
                    '& .MuiOutlinedInput-root': {
                        '& fieldset': {
                            borderColor: '#ffffff', // White outline color
                        },
                        '&:hover fieldset': {
                            borderColor: '#ffffff', // Maintain white outline color on hover
                        },
                        '&.Mui-focused fieldset': {
                            borderColor: '#ffffff', // Maintain white outline color when focused
                        },
                        '& input': {
                            color: '#000000', // Black text color for input
                        },
                    },
                },
                root: {
                    '& .MuiInputBase-root:hover .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'white',
                        color: 'white',
                    },
                    '& .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'white',
                        color: 'white',
                    },
                    '& .MuiInputBase-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'white',
                        color: 'white',
                    },
                }
            },
        },
    },
});

const localStorageDao = LocalStorageDao.getInstance()

function App() {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [location, setLocation] = useState<LocationResult | null>(null);
    const [favouriteLocation, setFavouriteLocation] = useState<FavouriteLocation[]>(
        Array.from(localStorageDao.getFavouriteLocations())
    );

    useEffect(() => {
        if (favouriteLocation.length > 0) {
            const defaultLocation = favouriteLocation[0]
            const location = new LocationResult()
            location.setLocationname(defaultLocation.locationName)
            location.setPlaceid(defaultLocation.placeId)

            setLocation(location)
        }
    }, []);

    const toggleDrawer = (open: boolean) => {
        setIsDrawerOpen(open);
    };

    const handleLocationSelected = (location: LocationResult) => {
        setLocation(location)
    }

    const isLocationAlreadyFavourited = (): boolean => {
        if (!location) {
            return false;
        }

        return favouriteLocation.find(f => f.placeId === location.getPlaceid()) !== undefined
    }

    const onAddFavouriteLocation = (location: FavouriteLocation) => {
        localStorageDao.addFavouriteLocation(location)

        const updatedList = [...favouriteLocation]
        updatedList.push(location)

        setFavouriteLocation(updatedList)
    }

    const onRemoveFavouriteLocation = (location: FavouriteLocation) => {
        localStorageDao.removeFavouriteLocation(location)

        const updatedList = favouriteLocation.filter(l => l.placeId !== location.placeId)

        setFavouriteLocation(updatedList)
    }

    return (
        <ThemeProvider theme={theme}>
            <div className={"sm:hidden"}>
                <AppBar>
                    <Toolbar>
                        <IconButton
                            edge="start"
                            color="inherit"
                            aria-label="menu"
                            onClick={() => toggleDrawer(true)}
                        >
                            <MenuIcon/>
                        </IconButton>
                        <Typography variant="h6" component="div">
                            AuroraWeather
                        </Typography>
                    </Toolbar>
                </AppBar>
            </div>
            <div className="flex background-image">
                <Drawer className={"backdrop-blur-md bg-white/20 pt-20"}
                        anchor="left"
                        open={isDrawerOpen}
                        onClose={() => toggleDrawer(false)}
                        PaperProps={{
                            sx: {
                                backgroundColor: "#88848473",
                                width: "80%",
                            }
                        }}
                >
                    <div className={"mt-20 px-5 text-center"}>
                        <SideMenu
                            onLocationSelected={handleLocationSelected}
                            favouriteLocations={favouriteLocation}
                        />
                    </div>
                </Drawer>
                <aside
                    className="h-screen sticky top-0 p-10 basis-1/3 backdrop-blur-md bg-white/20 pt-20 hidden md:block">
                    <SideMenu
                        onLocationSelected={handleLocationSelected}
                        favouriteLocations={favouriteLocation}
                    />
                </aside>
                <main className={"w-full sm:basis-2/3"}>
                    <Dashboard
                        location={location}
                        isLocationAlreadyFavourited={isLocationAlreadyFavourited()}
                        onAddLocationFavourite={onAddFavouriteLocation}
                        onRemoveLocationFavourite={onRemoveFavouriteLocation}
                    />
                </main>
            </div>
        </ThemeProvider>
    );
}

export default App;
