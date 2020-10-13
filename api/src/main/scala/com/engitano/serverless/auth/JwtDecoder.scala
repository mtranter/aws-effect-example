package com.engitano.serverless.auth

import com.auth0.jwk.Jwk
import cats.syntax.flatMap._
import cats.syntax.traverse._
import cats.syntax.either._
import cats.instances.list._
import cats.data.Kleisli
import cats.data.OptionT
import org.http4s.Request
import org.http4s.client.Client
import io.circe.generic.auto._
import io.circe.parser.decode
import org.http4s.circe.CirceEntityDecoder._
import scala.jdk.CollectionConverters._
import cats.effect.Sync
import org.http4s.Uri
import pdi.jwt.Jwt
import java.{util => ju}
import scala.util.Try

case class JwtClaims(sub: String, iat: Option[Long], exp: Option[Long], iss: String, aud: String)
case class JwkEndpointDto(keys: List[JwkJson])
case class JwkJson(alg: String, kty: String, use: String, n: String, e: String, kid: String, x5t: String, x5c: List[String]) {
  def toJava =
    Map[String, Any](
      "alg" -> this.alg,
      "kty" -> this.kty,
      "use" -> this.use,
      "n"   -> this.n,
      "e"   -> this.e,
      "kid" -> this.kid,
      "x5t" -> this.x5t,
      "x5x" -> this.x5c
    ).asJava
}

trait JwtDecoder[F[_], A] {
  def decodeToken(token: String): Either[AuthError, JwtClaims]
}

sealed trait AuthError     extends Exception
case object UnknownJwtKeyId extends AuthError
case class InvalidToken(msg: String, t: Option[Throwable] = None) extends AuthError {
  override def getMessage(): String  = msg
  override def getCause(): Throwable = t.getOrElse(null)
}

object JwtDecoder {

  case class HasKeyId(kid: String)
  type AuthUser[F[_], T] = Kleisli[OptionT[F, *], Request[F], T]

  def getJwks[F[_]: Sync](client: Client[F], jwkEndpoint: Uri): F[List[Jwk]] =
    client
      .expect[JwkEndpointDto](jwkEndpoint)
      .flatMap { _.keys.traverse[F, Jwk](t => Sync[F].delay(Jwk.fromValues(t.toJava))) }

  def getTokenKeyId[F[_]: Sync](token: String): Either[AuthError, String] =
    token
      .split("\\.")
      .headOption
      .toRight(InvalidToken("Empty Token"))
      .flatMap { headerToken =>
        Try(ju.Base64.getDecoder().decode(headerToken)).toEither
          .leftMap(t => new InvalidToken("Cannot decode token", Some(t)))
          .map(b => new String(b))
      }
      .flatMap { json =>
        decode[HasKeyId](json).map(_.kid).leftMap(e => InvalidToken("Cannot parse token header", Some(e)))
      }

  def apply[F[_]: Sync, User](jwks: List[Jwk]): JwtDecoder[F, User] =
    new JwtDecoder[F, User] {

      def _decodeToken(jwks: List[Jwk], token: String): Either[AuthError, JwtClaims] = {
        getTokenKeyId[F](token)
          .flatMap { id =>
            jwks
              .find(_.getId() == id)
              .toRight(UnknownJwtKeyId)
              .flatMap { jwk =>
                Jwt
                  .decode(token, jwk.getPublicKey())
                  .toEither
                  .leftMap(t => InvalidToken("Signing Error", Some(t)))
              }.flatMap(j => decode[JwtClaims](j.content).leftMap(e => InvalidToken("Invalid Token", Some(e))))
          }
      }

      override def decodeToken(token: String): Either[AuthError, JwtClaims] = _decodeToken(jwks, token)
    }
}
