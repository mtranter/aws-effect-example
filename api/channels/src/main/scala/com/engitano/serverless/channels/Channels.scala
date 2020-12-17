package com.engitano.serverless.channels

import com.engitano.awseffect.lambda.http4s.IOHttp4sLambda
import cats.effect.{Blocker, IO}
import org.http4s.implicits._
import io.chrisdavenport.vault.Key
import com.engitano.awseffect.lambda.http4s.LambdaRequestParams
import eu.timepit.refined.types.string.NonEmptyString
import com.engitano.serverless.channels.ChannelsRepo.ChannelsRepoConfig
import org.http4s.server.middleware.CORS
import cats.effect.ContextShift
import pureconfig.ConfigSource
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

case class Channel(
    id: NonEmptyString,
    creator: NonEmptyString,
    description: NonEmptyString,
    topic: NonEmptyString,
    `private`: Boolean,
    isArchived: Boolean
)

case class ChannelsConfig(repoConfig: ChannelsRepoConfig)

object ChannelsConfig {

  def load(blocker: Blocker)(implicit cs: ContextShift[IO]): IO[ChannelsConfig] =
    ConfigSource.default.loadF[IO, ChannelsConfig](blocker)
}

class ChannelsApiGateway extends IOHttp4sLambda {
  def lambdaHandler(blocker: Blocker, key: Key[LambdaRequestParams]) =
    ChannelsConfig.load(blocker).flatMap { cfg =>
      cfg.repoConfig.buildRepository().map(r => CORS(ChannelRoutes(r, key).orNotFound))
    }
}
