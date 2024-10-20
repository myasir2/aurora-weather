import {App, Duration, Stack, StackProps} from "aws-cdk-lib";
import {Vpc} from "aws-cdk-lib/aws-ec2";
import {Secret} from "aws-cdk-lib/aws-secretsmanager";
import {Code, Function, Runtime} from "aws-cdk-lib/aws-lambda";
import {OpenApiGatewayToLambda} from "@aws-solutions-constructs/aws-openapigateway-lambda";
import {Asset} from "aws-cdk-lib/aws-s3-assets";
import {getRegionalizedName} from "../../util/index";
import {Stage} from "../../types/index";

export interface DataProviderStackProps extends StackProps {
    readonly stage: Stage
    readonly codePath: string
    readonly apiDefinition: string
    readonly handler: string
    readonly vpc: Vpc
    readonly apiKeysSecret: Secret
}

export class DataProviderStack extends Stack {

    public readonly weatherApiLambda: Function
    public readonly xWeatherApiLambda: Function
    public readonly api: OpenApiGatewayToLambda

    public constructor(parent: App, id: string, props: DataProviderStackProps) {
        super(parent, id, props);

        const getConstructId = getRegionalizedName(props.stage)

        this.weatherApiLambda = this.createLambda(getConstructId("WeatherApiLambda"), props)
        this.xWeatherApiLambda = this.createLambda(getConstructId("XWeatherApiLambda"), props)

        props.apiKeysSecret.grantRead(this.weatherApiLambda)
        props.apiKeysSecret.grantRead(this.xWeatherApiLambda)

        this.api = new OpenApiGatewayToLambda(this, "OpenApiGatewayLambda", {
            apiDefinitionAsset: new Asset(this, "OpenApiGatewayLambdaAsset", {
                path: props.apiDefinition,
            }),
            apiIntegrations: [
                {
                    id: "WeatherApiHandler",
                    existingLambdaObj: this.weatherApiLambda,
                },
                {
                    id: "XWeatherHandler",
                    existingLambdaObj: this.xWeatherApiLambda,
                }
            ],
        })
    }

    private createLambda(name: string, props: DataProviderStackProps): Function {
        return new Function(this, name, {
            functionName: name,
            vpc: props.vpc,
            memorySize: 128,
            runtime: Runtime.NODEJS_18_X,
            code: Code.fromAsset(props.codePath),
            handler: props.handler,
            allowPublicSubnet: false,
            timeout: Duration.minutes(1),
            environment: {
                API_KEYS_SECRET_NAME: props.apiKeysSecret.secretName,
            },
        })
    }
}
