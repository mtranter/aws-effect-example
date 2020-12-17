package com.engitano.serverless.channels

import java.util.UUID

import cats.effect.IO
import cats.syntax.apply._
import cats.syntax.either._
import eu.timepit.refined.collection._
import eu.timepit.refined.refineV
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.auto._
import io.circe.refined._
import org.http4s.HttpRoutes
import org.http4s.ParseFailure
import org.http4s.QueryParamDecoder
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.dsl.io._
import org.http4s.headers.Location
import io.chrisdavenport.vault.Key
import com.engitano.awseffect.lambda.http4s.LambdaRequestParams

object ChannelRoutes {

  def toLower(nes: NonEmptyString) = refineV[NonEmpty].unsafeFrom(nes.value.toLowerCase())

  case class ListChannelsResponse(channels: List[Channel], nextToken: Option[Int])
  case class CreateChannelRequest(
      description: NonEmptyString,
      topic: NonEmptyString,
      `private`: Boolean,
      isArchived: Boolean
  ) {
    def toChannel(id: NonEmptyString, creator: NonEmptyString) = Channel(id, creator, description, toLower(topic), `private`, isArchived)
  }

  implicit val nonEmptyStringDecoder = QueryParamDecoder[String].emap(s => refineV[NonEmpty](s).leftMap(e => ParseFailure(s, e)))
  def getId()                        = IO(UUID.randomUUID()).map((_.toString())).flatMap(r => refineV[NonEmpty](r).leftMap(e => new Exception(e)).liftTo[IO])
  object NonEmptyStringVar {
    def unapply(str: String): Option[NonEmptyString] = refineV[NonEmpty](str).toOption
  }

  private object LastKeyQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("lastKey")

  def apply(repo: ChannelsRepo[IO], key: Key[LambdaRequestParams]): HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "channels" / NonEmptyStringVar(id) =>
          repo.getChannel(id).flatMap(r => Ok(r))
        case GET -> Root / "channels" / "topic" / NonEmptyStringVar(topic) :? LastKeyQueryParamMatcher(key) =>
          repo.listChannelsByTopic(toLower(topic), key).flatMap(r => Ok(r))
        case req @ POST -> Root / "channels" =>
          (getId(), req.as[CreateChannelRequest]).tupled.flatMap { case (id, c) =>
            req.attributes
              .lookup(key)
              .flatMap(_.proxyRequest.requestContext.authorizer)
              .flatMap(_.get[String]("email"))
              .flatMap(e => refineV[NonEmpty](e).toOption) match {
              case Some(e) =>
                repo.createChannel(c.toChannel(id, e)).flatMap(_ => Created(Location(Uri.uri("/channels") / id.value)))
              case None => InternalServerError()
            }

          }
      }
}
