package com.engitano.serverless.channels

import com.engitano.awseffect.lambda.http4s.IOHttp4sLambda
import cats.effect.{Blocker, IO}
import org.http4s.dsl.io._
import org.http4s.syntax.kleisli._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.{HttpRoutes}
import io.chrisdavenport.vault.Key
import com.engitano.awseffect.lambda.http4s.LambdaRequestParams
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig

case class Channel(id: String, creator: String, description: String, `private`: Boolean, isArchived: Boolean)

object Routes {
  def apply(): HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "channels" =>
          Ok(List(Channel("general", "mtranter", "The OG", false, false)))
      }
}

class Channels extends IOHttp4sLambda {
  def lambdaHandler(blocker: Blocker, key: Key[LambdaRequestParams]) = IO(CORS(Routes().orNotFound, CORSConfig(true, true, 3600000)))
}
