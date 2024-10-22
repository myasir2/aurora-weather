export type PartialRecord<K extends keyof any, T> = Partial<Record<K, T>>

export enum Stage {
    PROD = "prod"
}

export enum AwsRegion {
    US_EAST_1 = "us-east-1"
}
