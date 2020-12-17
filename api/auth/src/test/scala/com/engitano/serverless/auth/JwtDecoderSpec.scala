package com.engitano.serverless.auth

import org.scalatest._
import cats.instances.list._
import cats.syntax.traverse._
import matchers._
import wordspec.AnyWordSpec
import cats.effect.IO
import io.circe.parser.decode
import io.circe.generic.auto._
import pdi.jwt.JwtOptions

object Json {
  val JWK_JSON = """[
{
    "alg":"RS256",
    "kty":"RSA",
    "use":"sig",
    "n":"rWBu-u9yehdjq3Rr0Ho4mRnfwdKfhUtbkjJcYrJl2umCruOQJtQrIvotJL7R2puePQf03-frsr0C5CJ4wxxwIJgR_sTuQBDKE1swi_-9ynmxbBHDS4u8SnkvjRXwfkUqBj0M3I5GDILq76jtdhM7emXx8hXLU0WZUXa25Mb59KcbNB5uQUi0WpULY2TcB_M-PFp0hd4bzeklisSeqt_qYkGuExGHziFymq69PhZf0esvUnpqBrUjAyUT9OF0YnzmlgDcsVdaV4IsA7ZprVoxPXmT0cw34uAEmhNeYKw1K4_09Hg6935g9R6kZEUUdN4br70bidcSyK_jDo65w13MdQ",
    "e":"AQAB",
    "kid":"Z_Uty2vumEnM0PF9CAmnY",
    "x5t":"ynBD7VoAzEL1Mbglfn2ookZ_FcQ",
    "x5c":[
      "MIIDCTCCAfGgAwIBAgIJOOBXq1xoAXCJMA0GCSqGSIb3DQEBCwUAMCIxIDAeBgNVBAMTF2F3cy1lZmZlY3QuYXUuYXV0aDAuY29tMB4XDTIwMTAzMDA4MDQwMloXDTM0MDcwOTA4MDQwMlowIjEgMB4GA1UEAxMXYXdzLWVmZmVjdC5hdS5hdXRoMC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCtYG7673J6F2OrdGvQejiZGd/B0p+FS1uSMlxismXa6YKu45Am1Csi+i0kvtHam549B/Tf5+uyvQLkInjDHHAgmBH+xO5AEMoTWzCL/73KebFsEcNLi7xKeS+NFfB+RSoGPQzcjkYMgurvqO12Ezt6ZfHyFctTRZlRdrbkxvn0pxs0Hm5BSLRalQtjZNwH8z48WnSF3hvN6SWKxJ6q3+piQa4TEYfOIXKarr0+Fl/R6y9SemoGtSMDJRP04XRifOaWANyxV1pXgiwDtmmtWjE9eZPRzDfi4ASaE15grDUrj/T0eDr3fmD1HqRkRRR03huvvRuJ1xLIr+MOjrnDXcx1AgMBAAGjQjBAMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFFWo1fNjLCQCZpyPQv71/Ans8snjMA4GA1UdDwEB/wQEAwIChDANBgkqhkiG9w0BAQsFAAOCAQEAhEkUn93meG3s1VeK/HwRx2kpirmkSV+Kz8CaJbJ/f9B0la7mGqiWauDFIkSpe0mfV9Xzei0HMa3YZoQAOlDMYYbrJeB+qicdcN/WNaG0aH8ZND+KQ/HWH7SGhSjLaRHzgFQc+nMF7mtGeaviGbjtdh2uXfT3HkDFDzbvRFCZK7a36QTP3Valgld1U4Hl8DNSetGSotQTA0P+qYIYZSeU+tii+NMkQP2uLIZT8My8iEQlnT9T7qsnbEj54eGaaE8ykKxeliIB/pJG0tR5Db8ueHDNboF8f0ULhsIhcs3lrSWFP0+YTtfsFA+jcbcptsoY7+rV5vNgDw13F+bPQuG+wQ=="
    ]
},
{
    "alg":"RS256",
    "kty":"RSA",
    "use":"sig",
    "n":"4Ckt2EY8krajSYpmZiNmxWR3VK709L_35DN3L8nPPWnQiaiX3JwSFREEUxbjLixdOReExPH9M12X8606Uv_5OAsJcrf3mRRDM7BSC8frH1pYkOvAkhlwgIQ2CMxFDwgSkXQM991X5bI2OQMmpl5VcAQ07BJMIrAze9FMqQt7j0S3L_HGUrruFKS8-rbJZYcpyrTQgM8o2O8PV3G6C9AREiEriMD2_OlpEfbSUaGzZdAhfpZyDD8Ef3s7ZqXj9SijPtzv2ppiR7DKOQ-bML-UeNXKo0442XcvjwRynIM7m-2wbkgfhJu39M28bAIu5MCYdH0vjv1EdOHKrIN4YsXB8Q",
    "e":"AQAB",
    "kid":"IyFDkWVPe4vIjfjjzbpR2",
    "x5t":"qSBfsl-UAb0za6dqta9NAwSEf-A",
    "x5c":[
      "MIIDCTCCAfGgAwIBAgIJagkoMEuijp/0MA0GCSqGSIb3DQEBCwUAMCIxIDAeBgNVBAMTF2F3cy1lZmZlY3QuYXUuYXV0aDAuY29tMB4XDTIwMTAzMDA4MDQwM1oXDTM0MDcwOTA4MDQwM1owIjEgMB4GA1UEAxMXYXdzLWVmZmVjdC5hdS5hdXRoMC5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDgKS3YRjyStqNJimZmI2bFZHdUrvT0v/fkM3cvyc89adCJqJfcnBIVEQRTFuMuLF05F4TE8f0zXZfzrTpS//k4Cwlyt/eZFEMzsFILx+sfWliQ68CSGXCAhDYIzEUPCBKRdAz33VflsjY5AyamXlVwBDTsEkwisDN70UypC3uPRLcv8cZSuu4UpLz6tsllhynKtNCAzyjY7w9XcboL0BESISuIwPb86WkR9tJRobNl0CF+lnIMPwR/eztmpeP1KKM+3O/ammJHsMo5D5swv5R41cqjTjjZdy+PBHKcgzub7bBuSB+Em7f0zbxsAi7kwJh0fS+O/UR04cqsg3hixcHxAgMBAAGjQjBAMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFNH1maFt1TwPvH55uOWkZSeMnj6MMA4GA1UdDwEB/wQEAwIChDANBgkqhkiG9w0BAQsFAAOCAQEAfLQNLtSF30z3bnOWJBuD40KbDntYQUIcxDmLcNEGhqpg+IL6IoaIF5CH3Hd9T14XauCDQRgRA0sdl1yZ5AUvo7o0ilq1U0A0iHLgJiS1jEXD6gCIT4JdQotwF+VSWDm+3tCCszCM4a8sGbXCLdthjcG9weF0rVn1EWu+zYmu5yrllJI7dLgek0Sq+7qp9pmxa9DBrsNrdDFZfAzcnC5u9kHiynC5++G2mZraz2XwnV+env1TUnNyEol37GC/nqxVP7FBKNG0f29ueIr5WX/Bt856qkaC80Rnj8w2jyLSC3DmdkJW1YaLJ9dj3LndIcdo55prV+OMWNxOIYwqWlAUuQ=="
    ]
}]
"""
}
class JwtDecoderSpec extends AnyWordSpec with should.Matchers {
  "The Decoder" should {
    "decode a valid token" in {
      val keys = decode[List[JwkJson]](Json.JWK_JSON).right.get.traverse(JwkClient.fromJson[IO]).unsafeRunSync()
      val decoder = JwtDecoder[IO](keys, JwtOptions(expiration = false))
      val result = decoder.decodeToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlpfVXR5MnZ1bUVuTTBQRjlDQW1uWSJ9.eyJpc3MiOiJodHRwczovL2F3cy1lZmZlY3QuYXUuYXV0aDAuY29tLyIsInN1YiI6Imdvb2dsZS1vYXV0aDJ8MTEwMzc0OTE3MzYxMjEyMTM4MzY0IiwiYXVkIjpbImh0dHA6Ly9hcGkuYXdzLWVmZmVjdC5jb20uYXUiLCJodHRwczovL2F3cy1lZmZlY3QuYXUuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTYwODE2NTE4NiwiZXhwIjoxNjA4MjUxNTg2LCJhenAiOiIzSDNYMnNaVnF4RnNCNWczVGgyZUJWTmx5clpOZ0NNaiIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwifQ.aCNERyL04x1imZIjYar79GvlnzSAPCFU0wKyJb5Rf5QCXegnXdJS1oU7vRJG31OCzeQU3LesXu3Z5SV7zdvfe7rNLwnw7VhDIzOHCxi22_mPhoBRr31hPMIqqJs7HhStbri-2gLinVorcI_1e8e9tzzDy3D4LEn7Ir9uAbuGGUzIX0t1qY-6LdVVhPt6skce4H9vlw85mB-yzDAVhX8vR_n5290Rtbw9OtQhmpD44WiMWFXEAz04rl6XGioZx2jR-ES_Z9jIOmHnzQz3_jTvHbrQBJcx_xW66IepeCfdq3ObMYEQR5Zg8V0Bo4oZleOHahIPdKhxHNYZLP0_j--nZA")
      
      result should matchPattern {
        case Right(_) =>
      } 
    }
   }
}