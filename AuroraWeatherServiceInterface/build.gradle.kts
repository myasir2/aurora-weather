import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.20"
    id("com.google.protobuf") version "0.9.4"
    id("idea")
}

group = "ca.myasir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.protobuf:protobuf-java:4.28.2") // Protobuf Java runtime
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2") // Protobuf Kotlin support
    implementation("io.grpc:grpc-protobuf:1.68.0")
    implementation("io.grpc:grpc-stub:1.68.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2" // Specify the version of protoc
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.0"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("kotlin")

                // This section generates TypeScript code
                id("js") {
                    outputSubDir = "grpc-web"
                    option("import_style=commonjs")
                    option("binary")
                }
                id("ts") {
                    outputSubDir = "js"
                    option("ts_proto_opt=esModuleInterop=true,forceLong=string")
                }
            }

            it.plugins {
                id("grpc") {}
                id("grpc-web") {
                    option("mode=grpcwebtext,import_style=commonjs+dts")
                }
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(17) // Specify the JVM version if needed (e.g., 17)
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}
