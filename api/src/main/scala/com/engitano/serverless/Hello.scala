package com.engitano.serverless

import com.engitano.awseffect.lambda.http4s.IOHttp4sLambda
import cats.effect.{Blocker, IO}
import org.http4s.syntax.all._
import org.http4s.dsl.io._
import org.http4s.{ HttpRoutes }
import io.chrisdavenport.vault.Key
import com.engitano.awseffect.lambda.http4s.LambdaRequestParams

object Routes {
  def apply(): HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case GET -> Root => Ok("Hello, World. From Scala.")
      }
}

class HelloWorldLambda extends IOHttp4sLambda {
  def lambdaHandler(blocker: Blocker, key: Key[LambdaRequestParams]) = IO(Routes().orNotFound)
}
