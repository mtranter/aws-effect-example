package com.engitano.serverless.auth

import scala.jdk.CollectionConverters._
import cats.effect.Sync
import cats.instances.list._
import cats.syntax.flatMap._
import cats.syntax.traverse._
import com.auth0.jwk.Jwk
import io.circe.generic.auto._
import org.http4s.Uri
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client

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

trait JwkClient[F[_]] {
  def fetchJwks(jwkEndpoint: Uri): F[List[Jwk]]
}

object JwkClient {
  def apply[F[_]: Sync](client: Client[F]): JwkClient[F] = new JwkClient[F] {
    def fetchJwks(jwkEndpoint: Uri): F[List[Jwk]] =
      client
        .expect[JwkEndpointDto](jwkEndpoint)
        .flatMap { _.keys.traverse[F, Jwk](t => Sync[F].delay(Jwk.fromValues(t.toJava))) }
  }

}
