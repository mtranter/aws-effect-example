package com.engitano.serverless.dynamo

import cats.effect.IO
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

case class DynamoDBConfig(region: String, urlOverride: Option[URI]) {
  def buildClient(): IO[DynamoDbAsyncClient] =
    IO {
      val builder    = DynamoDbAsyncClient.builder()
      val withUrl    = urlOverride.fold(builder)(builder.endpointOverride)
      val withRegion = withUrl.region(Region.of(region))
      val withCredentials =
        if (urlOverride.exists(_.getHost().toLowerCase().contains("localhost")))
          withRegion.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("key", "secret")))
        else
          withRegion
      withCredentials.build()
    }
}
