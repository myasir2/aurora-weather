import {Stage} from "../../types";
import {App, Stack, StackProps} from "aws-cdk-lib";
import {Cluster} from "aws-cdk-lib/aws-ecs";
import {Vpc} from "aws-cdk-lib/aws-ec2";

export interface EcsClusterStackProps extends StackProps {
    readonly stage: Stage,
    readonly vpc: Vpc
}

export class EcsClusterStack extends Stack {

    public readonly cluster: Cluster

    public constructor(parent: App, id: string, props: EcsClusterStackProps) {
        super(parent, id, props);

        this.cluster = new Cluster(this, "Cluster", {
            vpc: props.vpc,
            containerInsights: true,
        })
    }
}

