import {Stage} from "../../types";
import {Cluster, ContainerImage, LogDriver} from "aws-cdk-lib/aws-ecs";
import {App, Duration, RemovalPolicy, Stack, StackProps} from "aws-cdk-lib";
import {ApplicationLoadBalancedFargateService} from "aws-cdk-lib/aws-ecs-patterns";
import {Repository} from "aws-cdk-lib/aws-ecr";
import {LogGroup, RetentionDays} from "aws-cdk-lib/aws-logs";
import {
    ApplicationProtocol,
    ApplicationProtocolVersion,
    Protocol as ElbProtocol
} from "aws-cdk-lib/aws-elasticloadbalancingv2";
import {CnameRecord, IPublicHostedZone} from "aws-cdk-lib/aws-route53";
import {DnsValidatedCertificate} from "aws-cdk-lib/aws-certificatemanager";
import {Effect, Policy, PolicyStatement} from "aws-cdk-lib/aws-iam";
import {SpecRestApi} from "aws-cdk-lib/aws-apigateway";

export interface EcsServiceStackProps extends StackProps {
    readonly stage: Stage
    readonly cluster: Cluster
    readonly ecrRepository: Repository
    readonly hostedZone: IPublicHostedZone
    readonly certificate: DnsValidatedCertificate
    readonly api: SpecRestApi
    readonly desiredCount?: number
    readonly additionalContainerEnvVars?: object
    readonly additionalTaskPolicies?: Policy[]
}

export class EcsServiceStack extends Stack {

    private readonly containerEnvVars = {}

    public readonly serviceName = "AuroraWeatherService"
    public readonly service: ApplicationLoadBalancedFargateService

    public constructor(parent: App, id: string, props: EcsServiceStackProps) {
        super(parent, id, props);

        const imageTag = process.env.SERVICE_IMAGE_TAG ?? "latest"

        // Set up container env vars
        this.containerEnvVars = {
            WEATHER_API_URL: `${props.api.url}/prod/weather-api`,
            X_WEATHER_API_URL: `${props.api.url}/prod/x-weather`,
        }

        this.service = new ApplicationLoadBalancedFargateService(this, "Service", {
            // Operational config
            cluster: props.cluster,
            cpu: 512,
            memoryLimitMiB: 1024,
            desiredCount: props.desiredCount,
            publicLoadBalancer: true,

            // Transport config
            listenerPort: 443,
            protocol: ApplicationProtocol.HTTPS,
            redirectHTTP: false,
            certificate: props.certificate,
            protocolVersion: ApplicationProtocolVersion.GRPC,
            targetProtocol: ApplicationProtocol.HTTPS,

            // Image config
            taskImageOptions: {
                image: ContainerImage.fromEcrRepository(props.ecrRepository, imageTag),
                containerName: "Application",
                containerPort: 9090,
                logDriver: LogDriver.awsLogs({
                    logGroup: this.createLogGroup("AppContainer-STDOUT"),
                    streamPrefix: "STDOUT-",
                }),
                environment: {
                    ...this.containerEnvVars,
                    ...props.additionalContainerEnvVars ?? {},
                },
            },
        })
        this.service.targetGroup.configureHealthCheck({
            interval: Duration.seconds(30),
            path: "/grpc.health.v1.Health", // Health check path for gRPC
            protocol: ElbProtocol.HTTPS,
            port: "9090",
            healthyThresholdCount: 2,
            unhealthyThresholdCount: 2,
        })

        // Attach additional policies
        props.additionalTaskPolicies?.forEach(policy => {
            this.service.taskDefinition.taskRole.attachInlinePolicy(policy)
        })
        this.service.taskDefinition.taskRole.attachInlinePolicy(new Policy(this, "ExecuteApiPolicy", {
            policyName: "ExecuteApiPolicy",
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ["execute-api:Invoke"],
                    resources: [
                        props.api.arnForExecuteApi()
                    ],
                })
            ],
        }))

        new CnameRecord(this, "ApiCnameRecord", {
            zone: props.hostedZone,
            domainName: this.service.loadBalancer.loadBalancerDnsName,
            ttl: Duration.seconds(172800),
            recordName: `aurora-weather-api.${props.hostedZone.zoneName}`,
        })
    }

    private createLogGroup(name: string): LogGroup {
        return new LogGroup(this, `${this.serviceName}-${name}`, {
            logGroupName: `${this.serviceName}-${name}`,
            removalPolicy: RemovalPolicy.RETAIN,
            retention: RetentionDays.ONE_MONTH,
        })
    }
}
