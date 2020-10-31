package com.engitano.serverless.channels

import com.engitano.dynamof.implicits._
import com.engitano.dynamof.formats.implicits._
import com.engitano.awseffect.lambda.http4s.IOHttp4sLambda
import cats.effect.{Blocker, IO}
import cats.syntax.applicative._
import org.http4s.dsl.io._
import org.http4s.syntax.kleisli._
import io.circe.generic.auto._
import io.circe.refined._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.{HttpRoutes}
import io.chrisdavenport.vault.Key
import com.engitano.awseffect.lambda.http4s.LambdaRequestParams
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig
import com.engitano.dynamof.Table
import eu.timepit.refined.types.string.NonEmptyString

case class Channel(id: NonEmptyString, creator: NonEmptyString, description: NonEmptyString, `private`: Boolean, isArchived: Boolean)

object Routes {

  val table = Table[Channel]("channels", 'id)

  def apply(): HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "channels" =>
          Ok(List(Channel(dyn"general", dyn"mtranter", dyn"The OG", false, false)))
      }
}

class Channels extends IOHttp4sLambda {
  def lambdaHandler(blocker: Blocker, key: Key[LambdaRequestParams]) = CORS(Routes().orNotFound, CORSConfig(true, true, 3600000)).pure[IO]
}
