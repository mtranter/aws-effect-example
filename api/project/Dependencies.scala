import sbt._

object Dependencies {

  def resolvers(): Seq[MavenRepository] =
    Seq(
      Resolver.bintrayRepo("engitano", "maven")
    )

  object Versions {
    val catsV              = "2.1.1"
    val awsEffectV         = "0.2.58"
    val dynamoDBV          = "0.5.148"
    val jwtV               = "4.2.0"
    val auth0V             = "0.13.0"
    val scalaTestV         = "3.2.2"
    val httpClientV        = "0.21.0"
    val pureConfigV        = "0.14.0"
    val refinedPureConfigV = "0.9.18"
    val circeV             = "0.13.0"
  }

  import Versions._

  val catsEffect        = "org.typelevel"         %% "cats-effect"              % catsV
  val http4sLambda      = "com.engitano"          %% "aws-effect-lambda-http4s" % awsEffectV
  val httpClient        = "org.http4s"            %% "http4s-blaze-client"      % httpClientV
  val http4sCirce       = "org.http4s"            %% "http4s-circe"             % httpClientV
  val http4sServer      = "org.http4s"            %% "http4s-server"            % httpClientV
  val dynamoDB          = "com.engitano"          %% "dynamo-f"                 % dynamoDBV
  val jwk               = "com.auth0"              % "jwks-rsa"                 % auth0V
  val pureConfig        = "com.github.pureconfig" %% "pureconfig"               % pureConfigV
  val pureConfigHttp4s  = "com.github.pureconfig" %% "pureconfig-http4s"        % pureConfigV
  val pureConfigCats    = "com.github.pureconfig" %% "pureconfig-cats-effect"   % pureConfigV
  val refinedPureConfig = "eu.timepit"            %% "refined-pureconfig"       % refinedPureConfigV
  val jwt               = "com.pauldijou"         %% "jwt-circe"                % jwtV
  val circe             = "io.circe"              %% "circe-refined"            % circeV

  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV
}
