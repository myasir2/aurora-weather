import {Stage} from "../../types/index";
import {Duration, Stack, StackProps} from "aws-cdk-lib";
import {Construct} from "constructs";
import {
    AllowedMethods,
    CacheCookieBehavior,
    CacheHeaderBehavior,
    CachePolicy,
    CacheQueryStringBehavior,
    Distribution,
    GeoRestriction,
    LambdaEdgeEventType,
    OriginProtocolPolicy,
    OriginRequestCookieBehavior,
    OriginRequestHeaderBehavior,
    OriginRequestPolicy,
    OriginRequestQueryStringBehavior,
    OriginSslPolicy,
    PriceClass,
    SecurityPolicyProtocol,
    ViewerProtocolPolicy
} from "aws-cdk-lib/aws-cloudfront";
import {Certificate} from "aws-cdk-lib/aws-certificatemanager";
import {HttpOrigin} from "aws-cdk-lib/aws-cloudfront-origins";
import {ARecord, IHostedZone, RecordTarget} from "aws-cdk-lib/aws-route53";
import {CloudFrontTarget} from "aws-cdk-lib/aws-route53-targets";
import {getRegionalizedName} from "../../util/index";
import {Code, Function, Runtime, Version} from "aws-cdk-lib/aws-lambda";
import {ManagedPolicy} from "aws-cdk-lib/aws-iam";
import {experimental} from "aws-cdk-lib/aws-cloudfront";
import {HOSTED_ZONE_CONFIG} from "../../config/resouce_config";

export interface CloudFrontStackProps extends StackProps {
    readonly stage: Stage
    readonly distributionDomain: string
    readonly bucketWebsiteDomain: string
    readonly referrerHeaderValue: string
    readonly backendApiDomain: string
    readonly edgeLambdaPythonCodePath: string
    readonly edgeLambdaPythonCodeHandler: string
    readonly acmCertificate: Certificate
    readonly hostedZone: IHostedZone
}

export class CloudFrontStack extends Stack {

    public readonly cloudFront: Distribution

    constructor(scope: Construct, id: string, props: CloudFrontStackProps) {
        super(scope, id, props);

        const getConstructId = getRegionalizedName(props.stage)
        const {zoneName,} = HOSTED_ZONE_CONFIG[props.stage]!

        const backendApiOriginEdgeLambda = new experimental.EdgeFunction(this, "BackendApiOriginEdgeLambda", {
            runtime: Runtime.PYTHON_3_9,
            code: Code.fromAsset(props.edgeLambdaPythonCodePath),
            handler: props.edgeLambdaPythonCodeHandler,
            timeout: Duration.seconds(3),
            functionName: getConstructId("backend-api-origin-lambda-edge"),
        })

        this.cloudFront = new Distribution(this, "CloudFrontDistribution", {
            defaultRootObject: "index.html",
            domainNames: [
                props.distributionDomain
            ],
            geoRestriction: GeoRestriction.allowlist("US", "CA"),
            certificate: props.acmCertificate,
            priceClass: PriceClass.PRICE_CLASS_ALL,
            minimumProtocolVersion: SecurityPolicyProtocol.TLS_V1_2_2021,
            defaultBehavior: {
                origin: new HttpOrigin(props.bucketWebsiteDomain, {
                    customHeaders: {
                        Referer: props.referrerHeaderValue,
                    },
                    protocolPolicy: OriginProtocolPolicy.HTTP_ONLY,
                }),
                allowedMethods: AllowedMethods.ALLOW_ALL,
                viewerProtocolPolicy: ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
            },
            additionalBehaviors: {
                "api*": {
                    origin: new HttpOrigin(props.backendApiDomain, {
                        protocolPolicy: OriginProtocolPolicy.HTTPS_ONLY,
                        originSslProtocols: [
                            OriginSslPolicy.TLS_V1_2
                        ],
                    }),
                    allowedMethods: AllowedMethods.ALLOW_ALL,
                    viewerProtocolPolicy: ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
                    originRequestPolicy: new OriginRequestPolicy(this, "OriginRequestPolicy", {
                        originRequestPolicyName: getConstructId("backend-api-origin-cloudfront-origin-request-policy"),
                        headerBehavior: OriginRequestHeaderBehavior.allowList(
                            "Accept-Charset",
                            "Accept",
                            "Referer",
                            "CloudFront-Viewer-TLS"
                        ),
                        cookieBehavior: OriginRequestCookieBehavior.all(),
                        queryStringBehavior: OriginRequestQueryStringBehavior.all(),
                    }),
                    cachePolicy: new CachePolicy(this, "CachePolicy", {
                        cachePolicyName: getConstructId("backend-api-origin-cloudfront-cache-policy"),
                        headerBehavior: CacheHeaderBehavior.allowList("Authorization"),
                        cookieBehavior: CacheCookieBehavior.all(),
                        queryStringBehavior: CacheQueryStringBehavior.all(),
                        minTtl: Duration.seconds(0),
                        maxTtl: Duration.seconds(1),
                        defaultTtl: Duration.seconds(0),
                    }),
                    edgeLambdas: [
                        {
                            eventType: LambdaEdgeEventType.VIEWER_RESPONSE,
                            functionVersion: backendApiOriginEdgeLambda.currentVersion,
                        },
                        {
                            eventType: LambdaEdgeEventType.ORIGIN_REQUEST,
                            functionVersion: backendApiOriginEdgeLambda.currentVersion,
                            includeBody: true,
                        },
                        {
                            eventType: LambdaEdgeEventType.ORIGIN_RESPONSE,
                            functionVersion: backendApiOriginEdgeLambda.currentVersion,
                        }
                    ],
                },
            },
        })

        new ARecord(this, "SiteAliasRecord", {
            zone: props.hostedZone,
            recordName: zoneName,
            target: RecordTarget.fromAlias(new CloudFrontTarget(this.cloudFront)),
        })
    }
}
