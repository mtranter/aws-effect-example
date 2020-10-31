import Dependencies._
// import com.lightbend.sbt.SbtProguard._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.engitano"
ThisBuild / organizationName := "Engitano"

lazy val root = (project in file("."))
  .settings(
    name := "AWS Effect Example",
    sbt.Keys.resolvers ++= Dependencies.resolvers(),
    Common.settings(),
    // Assembly.settings,
    libraryDependencies ++= Seq(
      http4sLambda,
      dynamoDB,
      jwt,
      jwk,
      circe,
      http4sCirce,
      http4sServer,
      httpClient,
      scalaTest % Test
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
  ).enablePlugins(SbtProguard)
  .settings(
    javaOptions in (Proguard, proguard) := Seq("-Xmx10g"),
    proguardInputs in Proguard := (dependencyClasspath in Compile).value.files, 
    proguardFilteredInputs in Proguard ++= ProguardOptions.noFilter((packageBin in Compile).value),
    proguardOptions in Proguard ++= Seq(
      "-dontoptimize",
      "-dontnote",
      "-ignorewarnings",
      "-dontobfuscate",
      // our entry method
      """-keep public class com.amazonaws.services.lambda.runtime.** { public *; }""",
      """-keep class * implements com.engitano.awseffect.lambda.catsio.IOLambda""",
      """-keep class scala.** { *; }"""
    )
  )