import {Stage} from "../types";

export const getRegionalizedName = (stage: Stage) => {
    return (name: string) => `aurora-weather-service-${name}-${stage}`.toLowerCase()
}
