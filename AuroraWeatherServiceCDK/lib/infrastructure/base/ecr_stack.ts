import {Stage} from "../../types/index";
import {App, Stack, StackProps} from "aws-cdk-lib";
import {Repository} from "aws-cdk-lib/aws-ecr";

export interface EcrStackProps extends StackProps {
    readonly stage: Stage
    readonly repositoryName: string
}

export class EcrStack extends Stack {

    public readonly repositoryName: string
    public readonly repository: Repository

    public constructor(parent: App, id: string, props: EcrStackProps) {
        super(parent, id, props);

        this.repositoryName = props.repositoryName

        this.repository = new Repository(this, "Ecr", {
            repositoryName: this.repositoryName,
        })
    }
}