import {Stage} from "../../types";
import {Vpc} from "aws-cdk-lib/aws-ec2";
import {App, Stack, StackProps} from "aws-cdk-lib";

export interface VpcStackProps extends StackProps {
    readonly stage: Stage
}

export class VpcStack extends Stack {

    public readonly vpc: Vpc

    public constructor(parent: App, id: string, props: VpcStackProps) {
        super(parent, id, props);

        this.vpc = new Vpc(this, "VPC", {
            maxAzs: 2,
        })
    }
}