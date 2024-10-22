import {Stage} from "../types";

export const getRegionalizedName = (stage: Stage) => {
    return (name: string) => `aurora-weather-website-${name}-${stage}`.toLowerCase()
}
