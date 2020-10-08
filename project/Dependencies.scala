import sbt._

object Dependencies {

  def resolvers(): Seq[MavenRepository] =
    Seq(
      Resolver.bintrayRepo("engitano", "maven")
    )

  object Versions {
    val awsEffectV = "0.2.54"
    val dynamoDBV  = "0.2.98"
    val scalaTestV = "3.2.2"
  }

  import Versions._

  val http4sLambda = "com.engitano" %% "aws-effect-lambda-http4s" % awsEffectV
  val dynamoDB     = "com.engitano" %% "dynamo-f"                 % dynamoDBV

  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV
}
