import {AwsRegion, PartialRecord, Stage} from "../types";
import {Environment} from "aws-cdk-lib";
import {HOSTED_ZONE_CONFIG} from "./resouce_config";

export interface DeploymentConfig {
    readonly stage: Stage
    readonly env: Environment
    readonly backendDomain: string
    readonly websiteDomain: string
}

export interface DeploymentStage {
    readonly config: DeploymentConfig
}

export interface DependencyDeploymentConfig<T> extends PartialRecord<Stage, T> {
}

export const DEPLOYMENT_STAGES: DeploymentStage[] = [
    {
        config: {
            stage: Stage.PROD,
            backendDomain: "aurora-weather-api.myasir.ca",
            websiteDomain: HOSTED_ZONE_CONFIG[Stage.PROD]!.zoneName,
            env: {
                account: "635935268016",
                region: AwsRegion.US_EAST_1
            }
        }
    }
]
