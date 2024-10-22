import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import {DEPLOYMENT_STAGES} from "./config/deployment_config";
import {VpcStack} from "./infrastructure/vpc/vpc_stack";
import {getRegionalizedName} from "./util/index";
import {SecretsManagerStack} from "./infrastructure/base/secrets_manager_stack";
import * as path from "path";
import {DataProviderStack} from "./infrastructure/base/data_provider_stack";
import {LocationServiceStack} from "./infrastructure/base/location_service_stack";
import {EcrStack} from "./infrastructure/base/ecr_stack";
import {HostedZoneStack} from "./infrastructure/base/hosted_zone_stack";
import {AcmStack} from "./infrastructure/base/acm_stack";
import {EcsClusterStack} from "./infrastructure/base/ecs_cluster_stack";
import {EcsServiceStack} from "./infrastructure/base/ecs_service_stack";

const app = new cdk.App();

DEPLOYMENT_STAGES.forEach(deploymentStage => {
    const deploymentConfig = deploymentStage.config
    const {stage, env,} = deploymentConfig
    const getConstructId = getRegionalizedName(stage)

    const vpcStack = new VpcStack(app, getConstructId("VpcStack"), {
        stage,
        env,
    })

    const ecrStack = new EcrStack(app, getConstructId("EcrStack"), {
        stage,
        env,
        repositoryName: "auroraweather",
    })

    const secretsManagerStack = new SecretsManagerStack(app, getConstructId("SecretsManagerStack"), {
        stage,
        env,
        vpc: vpcStack.vpc,
    })

    const hostedZoneStack = new HostedZoneStack(app, getConstructId("HostedZoneStack"), {
        stage,
        env,
    })

    const acmStack = new AcmStack(app, getConstructId("AcmStack"), {
        env,
        hostedZone: hostedZoneStack.hostedZone,
    })

    const ecsClusterStack = new EcsClusterStack(app, getConstructId("EcsClusterStack"), {
        stage,
        env,
        vpc: vpcStack.vpc,
    })

    const locationServiceStack = new LocationServiceStack(app, getConstructId("LocationServiceStack"), {
        stage,
        env,
    })

    const dataProviderStack = new DataProviderStack(app, getConstructId("WeatherDataProviderStack"), {
        env,
        stage,
        vpc: vpcStack.vpc,
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
        apiDefinition: path.join(
            __dirname,
            "..",
            "..",
            "WeatherDataProviderLambdaModel",
            "build",
            "output",
            "source",
            "openapi",
            "WeatherDataProviderService.openapi.json"
        ),
    })

    new EcsServiceStack(app, getConstructId("EcsServiceStack"), {
        stage,
        env,
        ecrRepository: ecrStack.repository,
        cluster: ecsClusterStack.cluster,
        hostedZone: hostedZoneStack.hostedZone,
        certificate: acmStack.certificate,
        api: dataProviderStack.api.apiGateway,
        additionalContainerEnvVars: {
            LOCATION_SERVICE_INDEX_NAME: locationServiceStack.herePlaceIndex.indexName,
        },
        additionalTaskPolicies: [
            locationServiceStack.indexPolicy
        ],
    })
})
