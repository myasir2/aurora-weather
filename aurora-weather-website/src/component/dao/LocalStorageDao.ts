import {Coordinates, LocationResult, WeatherProvider} from "../../proto/main_pb";

const WEATHER_PROVIDER_KEY = "weather_provider"
const FAVOURITE_LOCATION_KEY = "location_key"
const DEFAULT_PROVIDER = WeatherProvider.WEATHER_API.toString()

export interface FavouriteLocation {
    readonly placeId: string
    readonly locationName: string
    readonly longitude: number
    readonly latitude: number
}

export default class LocalStorageDao {

    private static instance: LocalStorageDao;

    public static getInstance(): LocalStorageDao {
        return this.instance || (this.instance = new this());
    }

    public setProvider(provider: WeatherProvider) {
        localStorage.setItem(WEATHER_PROVIDER_KEY, provider.toString());
    }

    public getProvider(): string {
        return localStorage.getItem(WEATHER_PROVIDER_KEY) ?? DEFAULT_PROVIDER
    }

    public getFavouriteLocations(): FavouriteLocation[] {
        const json = localStorage.getItem(FAVOURITE_LOCATION_KEY)

        if (!json) {
            return []
        }

        return Array.from(JSON.parse(json))
    }

    public addFavouriteLocation(favouriteLocation: FavouriteLocation) {
        const existing = this.getFavouriteLocations()

        console.log(existing)

        existing.push(favouriteLocation)

        localStorage.setItem(FAVOURITE_LOCATION_KEY, JSON.stringify(existing))
    }

    public removeFavouriteLocation(favouriteLocation: FavouriteLocation) {
        const existing = new Set(this.getFavouriteLocations())

        existing.delete(favouriteLocation)

        localStorage.setItem(FAVOURITE_LOCATION_KEY, JSON.stringify(existing))
    }
}
