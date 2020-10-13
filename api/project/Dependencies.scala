import sbt._

object Dependencies {

  def resolvers(): Seq[MavenRepository] =
    Seq(
      Resolver.bintrayRepo("engitano", "maven")
    )

  object Versions {
    val awsEffectV  = "0.2.58"
    val dynamoDBV   = "0.2.98"
    val jwtV        = "4.2.0"
    val auth0V      = "0.13.0"
    val scalaTestV  = "3.2.2"
    val httpClientV = "0.21.0"
  }

  import Versions._

  val http4sLambda = "com.engitano"  %% "aws-effect-lambda-http4s" % awsEffectV
  val httpClient   = "org.http4s"    %% "http4s-blaze-client"      % httpClientV
  val http4sCirce  = "org.http4s"    %% "http4s-circe"             % httpClientV
  val http4sServer = "org.http4s"    %% "http4s-server"            % httpClientV
  val dynamoDB     = "com.engitano"  %% "dynamo-f"                 % dynamoDBV
  val jwk          = "com.auth0"      % "jwks-rsa"                 % auth0V
  val jwt          = "com.pauldijou" %% "jwt-circe"                % jwtV

  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV
}
