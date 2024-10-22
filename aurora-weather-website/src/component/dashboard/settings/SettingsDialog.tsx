import {
    Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    FormControl,
    FormHelperText, InputLabel, MenuItem, Select, SelectChangeEvent, TextField
} from "@mui/material";
import React, {useEffect, useMemo} from "react";
import SettingsIcon from "@mui/icons-material/Settings";
import BackendApiServiceDao from "../../dao/BackendApiServiceDao";
import {WeatherProvider} from "../../../proto/main_pb";
import LocalStorageDao from "../../dao/LocalStorageDao";

const api = BackendApiServiceDao.getInstance()
const localStorageDao = LocalStorageDao.getInstance()
const providerLabelMap = new Map([
    ["WEATHER_API", "Weather API"],
    ["X_WEATHER_API", "xWeather API"]
])

const SettingsDialog: React.FC = (props) => {
    const [isOpen, setIsOpen] = React.useState(false);
    const [providers, setProviders] = React.useState<JSX.Element[]>([]);
    const [selectedProvider, setSelectedProvider] = React.useState(localStorageDao.getProvider())
    const [isLoading, setIsLoading] = React.useState(true);

    useEffect(() => {
        api.getProviders().then(p => {
            const menuItems = p.map(provider => {
                return <MenuItem
                    key={provider}
                    value={provider}
                >
                    {providerLabelMap.get(provider)}
                </MenuItem>
            })

            setIsLoading(false)
            setProviders(menuItems)
        })
    }, []);

    if (isLoading) {
        return <CircularProgress/>;
    }

    return (
        <>
            <SettingsIcon className={"text-white"} onClick={() => {
                setIsOpen(!isOpen)
            }}/>
            <Dialog
                open={isOpen}
                onClose={() => setIsOpen(false)}
                fullWidth={true}
                maxWidth="xl"
                PaperProps={{
                    component: 'form',
                    onSubmit: (event: React.FormEvent<HTMLFormElement>) => {
                        event.preventDefault();
                        const formData = new FormData(event.currentTarget);
                        const formJson = Object.fromEntries((formData as any).entries());
                        const provider = formJson.provider;

                        localStorageDao.setProvider(provider)

                        setIsOpen(false)
                    },
                }}
            >
                <DialogTitle>Settings</DialogTitle>
                <DialogContent>
                    {/*<TextField*/}
                    {/*    autoFocus*/}
                    {/*    required*/}
                    {/*    margin="dense"*/}
                    {/*    id="name"*/}
                    {/*    name="email"*/}
                    {/*    label="Email Address"*/}
                    {/*    type="email"*/}
                    {/*    fullWidth*/}
                    {/*    variant="standard"*/}
                    {/*/>*/}
                    <FormControl sx={{m: 1, minWidth: 120}} className={"w-full"}>
                        <InputLabel>Weather Provider</InputLabel>
                        <Select
                            value={selectedProvider}
                            label="Select Provider"
                            required={true}
                            id="provider"
                            name="provider"
                            onChange={(e) =>
                                setSelectedProvider((e.target.value as unknown as WeatherProvider).toString())
                            }
                        >
                            {providers}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setIsOpen(false)}>Cancel</Button>
                    <Button type="submit">Save</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}

export default SettingsDialog;
