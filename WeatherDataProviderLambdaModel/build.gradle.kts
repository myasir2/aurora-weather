plugins {
    kotlin("jvm") version "2.0.20"
    id("software.amazon.smithy") version "0.6.0" // Apply the Smithy plugin
}

group = "ca.myasir"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Smithy model generation tools and libraries
    implementation("software.amazon.smithy:smithy-model:1.30.0")
    implementation("software.amazon.smithy:smithy-utils:1.30.0")
    implementation("software.amazon.smithy:smithy-aws-traits:1.30.0")
    implementation("software.amazon.smithy:smithy-validation-model:1.30.0")

    // TypeScript codegen plugin
    implementation("software.amazon.smithy.typescript:smithy-aws-typescript-codegen:0.24.0")

    // OpenAPI plugin for API Gateway support
    implementation("software.amazon.smithy:smithy-openapi:1.30.0")

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))  // Use Java 17 or any required version
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Disable JAR generation as we don't require it
tasks.named("smithyBuildJar") {
    enabled = false
}

// Configure Smithy build tasks
tasks.register("smithyBuild", Exec::class) {
    group = "smithy"
    description = "Run Smithy build"
    commandLine("smithy", "build")  // This runs the Smithy build command
}
