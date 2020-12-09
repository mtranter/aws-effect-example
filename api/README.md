# AWS Effect Example

## Build
Use the awesome Earthly tool for builds: https://earthly.dev/

## Proguard
The build uses Proguard to tree shake and shrink the jars as much as possible. This means `sbt proguard:proguard` must be run with Java 8.