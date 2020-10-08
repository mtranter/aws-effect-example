import Dependencies._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.engitano"
ThisBuild / organizationName := "Engitano"

lazy val root = (project in file("."))
  .settings(
    name := "AWS Effect Example",
    sbt.Keys.resolvers ++= Dependencies.resolvers(),
    Common.settings(),
    Assembly.settings,
    libraryDependencies ++= Seq(
      http4sLambda,
      dynamoDB,
      scalaTest % Test
    )
  )
