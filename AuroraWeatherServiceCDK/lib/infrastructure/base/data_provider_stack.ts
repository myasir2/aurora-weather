import {App, Duration, Stack, StackProps} from "aws-cdk-lib";
import {Vpc} from "aws-cdk-lib/aws-ec2";
import {Secret} from "aws-cdk-lib/aws-secretsmanager";
import {Code, Function, Runtime} from "aws-cdk-lib/aws-lambda";

export interface DataProviderStackProps extends StackProps {
    readonly codePath: string
    readonly handler: string
    readonly lambdaName: string
    readonly vpc: Vpc
    readonly apiKeysSecret: Secret
}

export class DataProviderStack extends Stack {

    public constructor(parent: App, id: string, props: DataProviderStackProps) {
        super(parent, id, props);

        const lambda = new Function(this, "DataProviderLambda", {
            functionName: props.lambdaName,
            vpc: props.vpc,
            memorySize: 128,
            runtime: Runtime.NODEJS_18_X,
            code: Code.fromAsset(props.codePath),
            handler: props.handler,
            allowPublicSubnet: false,
            timeout: Duration.minutes(1),
            environment: {
                API_KEYS_SECRET_NAME: props.apiKeysSecret.secretName,
            }
        })

        props.apiKeysSecret.grantRead(lambda)
    }
}
