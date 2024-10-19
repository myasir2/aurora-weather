import {DependencyDeploymentConfig} from "./deployment_config";
import {Stage} from "../types/index";

export interface HostedZoneConfig {
    parentZoneName: string
    zoneName: string
}

export const HOSTED_ZONE_CONFIG: DependencyDeploymentConfig<HostedZoneConfig> = {
    [Stage.PROD]: {
        parentZoneName: "myasir.ca",
        zoneName: "aurora-weather-api.myasir.ca"
    }
}
