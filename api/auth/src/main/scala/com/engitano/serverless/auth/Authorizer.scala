package com.engitano.serverless.auth

import cats.effect.Blocker
import cats.effect.IO
import cats.syntax.apply._
import cats.syntax.either._
import com.engitano.awseffect.lambda.EffectfulLambda
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerResponse
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerTokenRequest
import com.engitano.awseffect.lambda.apigw.IAMPolicyDocument
import com.engitano.awseffect.lambda.apigw.IAMStatement
import com.engitano.awseffect.lambda.catsio.IOLambda
import io.circe.generic.auto._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.Uri
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.http4s._
import cats.effect.ContextShift
import pureconfig.module.catseffect.syntax._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

case class Config(jwksEndpoint: Uri)

object Config {

  def load(blocker: Blocker)(implicit cs: ContextShift[IO]): IO[Config] =
    ConfigSource.default.loadF[IO, Config](blocker)
}

class Authorizer extends IOLambda {
  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  import EffectfulLambda._

  def authHandler(jwtDecoder: JwtDecoder[IO]): EffectfulHandler[IO, CustomAuthorizerTokenRequest, CustomAuthorizerResponse] = (req, _) =>
    jwtDecoder
      .decodeToken(req.authorizationToken.drop("bearer ".size))
      .map(c => IO(CustomAuthorizerResponse(c.sub, IAMPolicyDocument(IAMStatement.allow("execute-api:Invoke", "*")))))
      .leftMap(t =>
        Logger[IO].warn(t)("Error decoding JWT") *> IO(
          CustomAuthorizerResponse("anon", IAMPolicyDocument(IAMStatement.deny("execute-api:Invoke", "*")))
        )
      )
      .merge

  override def handler(blocker: Blocker): IO[com.engitano.awseffect.lambda.LambdaHandler[IO]] =
    (BlazeClientBuilder[IO](blocker.blockingContext).allocated, Config.load(blocker)).tupled
      .flatMap(c => JwkClient[IO](c._1._1).fetchJwks(c._2.jwksEndpoint))
      .map(jwks => JwtDecoder[IO](jwks))
      .map(decoder => EffectfulLambda(blocker)(authHandler(decoder)))

}
