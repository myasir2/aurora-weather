plugins {
    java
    id("software.amazon.smithy.gradle.smithy-base") version "1.1.0"
}

group = "ca.myasir"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Smithy dependencies required during model build process
    smithyBuild("software.amazon.smithy:smithy-aws-apigateway-traits:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-aws-traits:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-model:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-utils:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-validation-model:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-aws-endpoints:1.52.0")
    smithyBuild("software.amazon.smithy:smithy-openapi-traits:1.52.0")


    // TypeScript and OpenAPI plugins
    smithyBuild("software.amazon.smithy.typescript:smithy-aws-typescript-codegen:0.24.0")
    smithyBuild("software.amazon.smithy:smithy-aws-apigateway-openapi:1.52.0")

    // Smithy dependencies required for IDE to work with smithy files
    implementation("software.amazon.smithy:smithy-aws-apigateway-traits:1.52.0")
    implementation("software.amazon.smithy:smithy-model:1.52.0")
    implementation("software.amazon.smithy:smithy-aws-traits:1.52.0")
    implementation("software.amazon.smithy:smithy-utils:1.52.0")
    implementation("software.amazon.smithy:smithy-aws-traits:1.52.0")
    implementation("software.amazon.smithy:smithy-validation-model:1.52.0")
    implementation("software.amazon.smithy:smithy-aws-endpoints:1.52.0")
    implementation("software.amazon.smithy:smithy-openapi-traits:1.52.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))  // Use Java 17 or any required version
    }
}

tasks.build {
    dependsOn("smithyBuild")
}
