import {App, Stack, StackProps} from "aws-cdk-lib";
import {Stage} from "../../types/index";
import {CfnPlaceIndex} from "aws-cdk-lib/aws-location";
import {getRegionalizedName} from "../../util/index";

export interface LocationServiceStackProps extends StackProps {
    readonly stage: Stage
}

export class LocationServiceStack extends Stack {

    public readonly herePlaceIndex: CfnPlaceIndex

    public constructor(parent: App, id: string, props: LocationServiceStackProps) {
        super(parent, id, props)

        const getConstructId = getRegionalizedName(props.stage)

        this.herePlaceIndex = new CfnPlaceIndex(this, getConstructId("here-place-index"), {
            indexName: getConstructId("here-place-index"),
            dataSource: "Here",
            pricingPlan: "RequestBasedUsage",
        })
    }
}
