import {AwsRegion, PartialRecord, Stage} from "../types";
import {Environment} from "aws-cdk-lib";

export interface DeploymentConfig {
    readonly stage: Stage
    readonly env: Environment
}

export interface DeploymentStage {
    readonly config: DeploymentConfig
}

export type DependencyDeploymentConfig<T> = PartialRecord<Stage, T>

export const DEPLOYMENT_STAGES: DeploymentStage[] = [
    {
        config: {
            stage: Stage.PROD,
            env: {
                account: "635935268016",
                region: AwsRegion.US_EAST_1,
            },
        },
    }
]
