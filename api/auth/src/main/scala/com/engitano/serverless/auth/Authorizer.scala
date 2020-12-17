package com.engitano.serverless.auth

import cats.effect.Blocker
import cats.effect.IO
import cats.syntax.apply._
import com.engitano.awseffect.lambda.EffectfulLambda
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerResponse
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerTokenRequest
import com.engitano.awseffect.lambda.apigw.IAMPolicyDocument
import com.engitano.awseffect.lambda.apigw.IAMStatement
import com.engitano.awseffect.lambda.catsio.IOLambda
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.Uri
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.http4s._
import cats.effect.ContextShift
import pureconfig.module.catseffect.syntax._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import java.time.ZonedDateTime
import org.http4s.client.Client
import org.http4s.Request
import org.http4s.Headers
import org.http4s.headers.Authorization
import org.http4s.Credentials.Token
import org.http4s.AuthScheme

case class Config(jwksEndpoint: Uri, oauthEndpoint: Uri)

object Config {

  def load(blocker: Blocker)(implicit cs: ContextShift[IO]): IO[Config] =
    ConfigSource.default.loadF[IO, Config](blocker)
}

case class UserInfo(
  sub: String, 
  given_name: String, 
  family_name: String, 
  nickname: String, 
  name: String, 
  picture: String, 
  local: String,
  updated_at: ZonedDateTime,
  email: String,
  email_verified: Boolean
)
trait Auth0Client {
  def getUserInfo(token: String): IO[UserInfo]
}
object Auth0Client {
  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]
  def apply(client: Client[IO], baseUri: Uri) = new Auth0Client {
    def getUserInfo(token: String): IO[UserInfo] = client
      .expect[UserInfo](Request[IO](uri = baseUri / "userinfo", headers = Headers.of(Authorization(Token(AuthScheme.Bearer, token)))))
      .handleErrorWith(t => {
        Logger[IO].error(t)("Error requesting user info") *> IO.raiseError(t)
      })
  }

}
class AuthorizerHandler extends IOLambda {
  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  import EffectfulLambda._

  def authHandler(authClient: Auth0Client): EffectfulHandler[IO, CustomAuthorizerTokenRequest, CustomAuthorizerResponse] = (req, _) =>
    authClient
      .getUserInfo(req.authorizationToken.drop("bearer ".size))
      .map(c => CustomAuthorizerResponse(c.sub, IAMPolicyDocument(IAMStatement.allow("execute-api:Invoke", "*")), Some(Map("email" -> c.email))))
      .handleErrorWith(t =>
        Logger[IO].warn(t)("Error decoding JWT") *> IO(
          CustomAuthorizerResponse("anon", IAMPolicyDocument(IAMStatement.deny("execute-api:Invoke", "*")))
        )
      )

  override def handler(blocker: Blocker): IO[com.engitano.awseffect.lambda.LambdaHandler[IO]] =
    (BlazeClientBuilder[IO](blocker.blockingContext).allocated, Config.load(blocker)).tupled
      .map(c => Auth0Client(c._1._1, c._2.oauthEndpoint))
      .map(client => EffectfulLambda(blocker)(authHandler(client)))

}
