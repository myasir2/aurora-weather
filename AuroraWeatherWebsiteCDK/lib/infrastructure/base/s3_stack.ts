import * as cdk from "aws-cdk-lib";
import {Construct} from "constructs";
import {Stack, StackProps} from "aws-cdk-lib";
import {getRegionalizedName} from "../../util/index";
import {BlockPublicAccess, Bucket, BucketPolicy} from "aws-cdk-lib/aws-s3";
import {Stage} from "../../types/index";

export interface S3StackProps extends StackProps {
    readonly referrerCode: string
    readonly stage: Stage
}

export class S3Stack extends Stack {

    public readonly websiteBucket: Bucket

    constructor(scope: Construct, id: string, props: S3StackProps) {
        super(scope, id, props);

        const regionalizedName = getRegionalizedName(props.stage)

        this.websiteBucket = new Bucket(this, "SPAWebsite", {
            websiteIndexDocument: "index.html",
            websiteErrorDocument: "index.html",
            blockPublicAccess: new BlockPublicAccess({
                restrictPublicBuckets: false,
            }),
            publicReadAccess: true,
            bucketName: regionalizedName("spa-bucket"),
        });

        const grant = this.websiteBucket.grantPublicAccess("*", "s3:GetObject");
        grant.resourceStatement!.addResources(this.websiteBucket.bucketArn);
        grant.resourceStatement!.sid = "AllowByRefererHeader";
        grant.resourceStatement!.addCondition("StringEquals", {
            "aws:Referer": props.referrerCode
        });
    }
}
