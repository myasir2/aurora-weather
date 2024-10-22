#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import * as path from "path";
import {DEPLOYMENT_STAGES} from "./config/deployment_config";
import {getRegionalizedName} from "./util/index";
import {HostedZoneStack} from "./infrastructure/base/hosted_zone_stack";
import {AcmStack} from "./infrastructure/base/acm_stack";
import {S3Stack} from "./infrastructure/base/s3_stack";
import {CloudFrontStack} from "./infrastructure/base/cloudfront_stack";

const app = new cdk.App();
DEPLOYMENT_STAGES.forEach(deploymentStage => {
    const deploymentConfig = deploymentStage.config
    const {stage, env, backendDomain, websiteDomain} = deploymentConfig
    const getConstructId = getRegionalizedName(stage)

    const hostedZoneStack = new HostedZoneStack(app, getConstructId("HostedZoneStack"), {
        env,
        stage
    })

    const acmStack = new AcmStack(app, getConstructId("AcmStack"), {
        env,
        hostedZone: hostedZoneStack.hostedZone
    })

    const s3Stack = new S3Stack(app, getConstructId("S3Stack"), {
        stage,
        env,
        referrerCode: "test123"
    })

    const cloudFrontStack = new CloudFrontStack(app, getConstructId("CloudFrontStack"), {
        stage,
        env,
        distributionDomain: websiteDomain,
        referrerHeaderValue: "test123",
        bucketWebsiteDomain: s3Stack.websiteBucket.bucketWebsiteDomainName,
        backendApiDomain: backendDomain,
        acmCertificate: acmStack.certificate,
        hostedZone: hostedZoneStack.hostedZone,
        edgeLambdaPythonCodePath: path.join(__dirname, "..", "CloudFrontEdgeLambda", "src"),
        edgeLambdaPythonCodeHandler: "index.handler"
    })
})
