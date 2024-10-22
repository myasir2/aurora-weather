import {IHostedZone, PublicHostedZone} from "aws-cdk-lib/aws-route53";
import {DnsValidatedCertificate} from "aws-cdk-lib/aws-certificatemanager";
import {App, Stack, StackProps} from "aws-cdk-lib";

export interface AcmStackProps extends StackProps {
    readonly hostedZone: IHostedZone
}

export class AcmStack extends Stack {

    public certificate: DnsValidatedCertificate

    public constructor(parent: App, id: string, props: AcmStackProps) {
        super(parent, id, props);

        // Create cert
        this.certificate = new DnsValidatedCertificate(this, "DnsValidatedCertificate", {
            domainName: `*.${props.hostedZone.zoneName}`,
            hostedZone: props.hostedZone,
            subjectAlternativeNames: [
                props.hostedZone.zoneName,
            ]
        })
    }
}
