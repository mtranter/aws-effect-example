package com.engitano.serverless.auth

import java.{util => ju}

import scala.util.Try

import cats.effect.Sync
import cats.syntax.either._
import com.auth0.jwk.Jwk
import io.circe.generic.auto._
import io.circe.parser.decode
import pdi.jwt.Jwt

case class JwtClaims(sub: String, iat: Option[Long], exp: Option[Long], iss: String, aud: String)

trait JwtDecoder[F[_]] {
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

  def apply[F[_]: Sync](jwks: List[Jwk]): JwtDecoder[F] =
    new JwtDecoder[F] {

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
