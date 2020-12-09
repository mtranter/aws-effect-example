import Dependencies._
// import com.lightbend.sbt.SbtProguard._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.engitano"
ThisBuild / organizationName := "Engitano"

lazy val commonSettings: Seq[Setting[_]] = Common.settings() ++ Assembly.settings() ++ Seq(
    sbt.Keys.resolvers ++= Dependencies.resolvers(),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
)

lazy val common = (project in file("common"))
  .settings(
    name := "Common",
    // Assembly.settings,
    libraryDependencies ++= Seq(
      dynamoDB,
      catsEffect,
      scalaTest % Test
    ),
  )
  .settings(commonSettings)

lazy val channels = (project in file("channels"))
  .dependsOn(common)
  .settings(
    name := "Channels Service",
    sbt.Keys.resolvers ++= Dependencies.resolvers(),
    Common.settings(),
    // Assembly.settings,
    libraryDependencies ++= Seq(
      http4sLambda,
      dynamoDB,
      pureConfig,
      pureConfigCats,
      refinedPureConfig,
      logging,
      circe,
      http4sCirce,
      http4sServer,
      scalaTest % Test
    ),
    addCompilerPlugin("io.tryp" % "splain" % "0.5.7" cross CrossVersion.patch)
  )
  .settings(commonSettings)


lazy val auth = (project in file("auth"))
  .settings(
    name := "Authorizer",
    sbt.Keys.resolvers ++= Dependencies.resolvers(),
    Common.settings(),
    // Assembly.settings,
    libraryDependencies ++= Seq(
      http4sLambda,
      httpClient,
      pureConfig,
      pureConfigCats,
      pureConfigHttp4s,
      catsEffect,
      logging,
      jwt,
      jwk,
      circe,
      http4sCirce,
      scalaTest % Test
    ),
  )
  .settings(commonSettings)
