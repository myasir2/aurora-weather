import {App, Stack, StackProps} from "aws-cdk-lib";
import {Stage} from "../../types/index";
import {Secret} from "aws-cdk-lib/aws-secretsmanager";
import {getRegionalizedName} from "../../util/index";
import {Vpc} from "aws-cdk-lib/aws-ec2";

export interface SecretsManagerStackProps extends StackProps {
    readonly stage: Stage
    readonly vpc: Vpc
}

export class SecretsManagerStack extends Stack {

    public readonly weatherApiKeysSecret: Secret

    public constructor(parent: App, id: string, props: SecretsManagerStackProps) {
        super(parent, id, props);

        const getConstructId = getRegionalizedName(props.stage)

        this.weatherApiKeysSecret = new Secret(this, "WeatherApiKeysSecret", {
            secretName: getConstructId("WeatherApiKeysSecret"),
            generateSecretString: {
                secretStringTemplate: JSON.stringify({}),
                generateStringKey: "token"
            }
        })
    }
}
