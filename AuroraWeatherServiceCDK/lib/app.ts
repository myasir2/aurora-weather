#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import {DEPLOYMENT_STAGES} from "./config/deployment_config";
import {VpcStack} from "./infrastructure/vpc/vpc_stack";
import {getRegionalizedName} from "./util/index";
import {SecretsManagerStack} from "./infrastructure/base/secrets_manager_stack";
import {DataProviderStack} from "./infrastructure/base/data_provider_stack";
import * as path from "path";

const app = new cdk.App();

DEPLOYMENT_STAGES.forEach(deploymentStage => {
    const deploymentConfig = deploymentStage.config
    const {stage, env} = deploymentConfig
    const getConstructId = getRegionalizedName(stage)

    const vpcStack = new VpcStack(app, getConstructId("VpcStack"), {
        stage,
        env
    })

    const secretsManagerStack = new SecretsManagerStack(app, getConstructId("SecretsManagerStack"), {
        stage,
        env,
        vpc: vpcStack.vpc
    })

    const dataProviderStack = new DataProviderStack(app, getConstructId("DataProviderStack"), {
        env,
        vpc: vpcStack.vpc,
        lambdaName: getConstructId("DataProviderLambda"),
        apiKeysSecret: secretsManagerStack.weatherApiKeysSecret,
        handler: "index.handler",
        codePath: path.join(
            __dirname,
            "..",
            "..",
            "WeatherDataProviderLambda",
            "dist",
            "index.js.zip"
        ),
    })
})
