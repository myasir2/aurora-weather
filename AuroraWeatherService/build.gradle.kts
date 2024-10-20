plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("idea")
    id("com.google.protobuf") version "0.9.2" // Add Protobuf plugin
}

group = "ca.myasir"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.google.protobuf:protobuf-java:4.28.2") // Protobuf Java runtime
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2") // Protobuf Kotlin support
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation(project(":AuroraWeatherServiceInterface"))
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.4")  // For SSL/TLS

    // Logging dependencies
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

    // AWS dependencies
    implementation("software.amazon.awssdk:core:2.20.100")
    implementation("software.amazon.awssdk:apigateway:2.20.100") // API Gateway support
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.20.100") // DDB support
    implementation("software.amazon.awssdk:dynamodb:2.20.100") // DDB support
    implementation("software.amazon.awssdk:location:2.20.100") // AmazonLocationService support

    // Util
    implementation("com.google.code.gson:gson:2.11.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}
