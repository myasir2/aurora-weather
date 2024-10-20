## Overview

This package contains the protobuf definition files for the main API.
The package is configured to generate type definitions for Java, Kotlin, and TypeScript.

## Generating Type Definitions

1. Ensure you have `protoc-gen-ts` installed for TypeScript definitions. You need to install it as a global package,
   which can be done via `npm install -g protoc-gen-ts`.
2. Run `./gradlew build` which will generate the type definitions in the `build` folder.
