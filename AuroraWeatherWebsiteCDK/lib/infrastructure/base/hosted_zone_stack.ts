import {App, Stack, StackProps} from "aws-cdk-lib";
import {Stage} from "../../types/index";
import {CrossAccountZoneDelegationRecord, HostedZone, IHostedZone, PublicHostedZone} from "aws-cdk-lib/aws-route53";
import {HOSTED_ZONE_CONFIG} from "../../config/resouce_config";
import {Role} from "aws-cdk-lib/aws-iam";

export interface HostedZoneStackProps extends StackProps {
    readonly stage: Stage
}

export class HostedZoneStack extends Stack {

    public readonly hostedZone: IHostedZone

    public constructor(parent: App, id: string, props: HostedZoneStackProps) {
        super(parent, id, props);

        const hostedZoneConfig = HOSTED_ZONE_CONFIG[props.stage]!

        this.hostedZone = PublicHostedZone.fromPublicHostedZoneAttributes(this, "HostedZone", {
            hostedZoneId: "Z05116747ZN04VFT88PH",
            zoneName: hostedZoneConfig.parentZoneName,
        })
    }
}
