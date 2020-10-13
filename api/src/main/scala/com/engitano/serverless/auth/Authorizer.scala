package com.engitano.serverless.auth

import com.engitano.awseffect.lambda.catsio.IOLambda
import cats.effect.{Blocker, IO}
import com.engitano.awseffect.lambda.EffectfulLambda
import io.circe.generic.auto._
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerTokenRequest
import com.engitano.awseffect.lambda.apigw.CustomAuthorizerResponse
import com.engitano.awseffect.lambda.apigw.IAMPolicyDocument
import com.engitano.awseffect.lambda.apigw.IAMStatement

class Authorizer extends IOLambda {

  import EffectfulLambda._

  val authHandler: EffectfulHandler[IO, CustomAuthorizerTokenRequest, CustomAuthorizerResponse] = (req, _) =>
    IO {
      CustomAuthorizerResponse(
        "like-a-boss",
        IAMPolicyDocument(IAMStatement.allow("execute-api:Invoke", req.methodArn)),
        context = Some(Map("userEmail" -> "hello@hello.com"))
      )
    }

  override def handler(blocker: Blocker): IO[com.engitano.awseffect.lambda.LambdaHandler[IO]] = IO(EffectfulLambda(blocker)(authHandler))
}
