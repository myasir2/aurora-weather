import {
    AuroraWeatherServiceClient
} from "../../proto/main_grpc_web_pb";
import {Empty} from "google-protobuf/google/protobuf/empty_pb";
import {
    Coordinates,
    GetLocationCoordinatesRequest,
    GetLocationsRequest, GetWeatherDataRequest,
    LocationResult,
    WeatherData, WeatherProvider
} from "../../proto/main_pb";

export default class BackendApiServiceDao {

    private grpcHost = ''; // You
    private static instance: BackendApiServiceDao;
    private client = new AuroraWeatherServiceClient(this.grpcHost, null, null);

    public static getInstance(): BackendApiServiceDao {
        return this.instance || (this.instance = new this());
    }

    public async getProviders(): Promise<string[]> {
        return await new Promise((resolve, reject) => {
            this.client.getWeatherProviders(new Empty(), undefined, (err, response) => {
                if (err) {
                    console.error(`Error while getting providers from API: ${err.message}`)

                    reject(Error("Sorry, we're having some issues getting weather provider information. Please try again"))
                } else {
                    resolve(response.getProvidersList())
                }
            })
        })
    }

    public async searchLocations(searchText: string): Promise<LocationResult[]> {
        const request = new GetLocationsRequest()
        request.setSearchtext(searchText)

        return await new Promise((resolve, reject) => {
            this.client.getLocations(request, undefined, (err, response) => {
                if (err) {
                    console.error(`Error while getting locations from API: ${err.message}`)

                    reject(Error("Sorry, we're having some technical difficulties. Please try again"))
                } else {
                    resolve(response.getResultsList())
                }
            })
        })
    }

    public async getCoordinates(placeId: string): Promise<Coordinates> {
        const request = new GetLocationCoordinatesRequest()
        request.setPlaceid(placeId)

        return await new Promise((resolve, reject) => {
            this.client.getLocationCoordinates(request, undefined, (err, response) => {
                const coordinates = response.getCoordinates()

                if (err) {
                    console.error(`Error while getting coordinates from API: ${err.message}`)

                    reject(Error(`Sorry, an error occurred while getting the forecast. Please try again.`))
                } else if (!coordinates) {
                    console.error(`Coordinates not found for placeId: ${placeId}`)

                    reject(Error("Sorry, an error occurred while getting the forecast. Please try again."))
                } else if (coordinates) {
                    resolve(coordinates)
                }
            })
        })
    }

    public async getForecast(coordinates: Coordinates, provider: WeatherProvider): Promise<WeatherData[]> {
        const request = new GetWeatherDataRequest()
        request.setLongitude(coordinates.getLongitude())
        request.setLatitude(coordinates.getLatitude())
        request.setProvider(provider)

        return await new Promise((resolve, reject) => {
            this.client.getWeatherData(request, undefined, (err, response) => {
                if (err) {
                    console.error(`Error while getting weather from API: ${err.message}`)

                    reject(Error("Sorry, we're having technical difficulties. Please try another weather provider, or try again later"))
                } else {
                    resolve(response.getForecastList())
                }
            })
        })
    }
}
