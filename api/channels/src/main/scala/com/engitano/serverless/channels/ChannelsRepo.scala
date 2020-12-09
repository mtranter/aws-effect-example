package com.engitano.serverless.channels

import cats.effect.IO
import cats.~>
import com.engitano.dynamof.Table
import com.engitano.dynamof._
import com.engitano.dynamof.implicits._
import com.engitano.dynamof.formats.FromDynamoValue
import com.engitano.dynamof.formats.ToDynamoMap
import com.engitano.dynamof.formats.auto._
import com.engitano.serverless.channels.ChannelRoutes.ListChannelsResponse
import com.engitano.serverless.dynamo.DynamoDBConfig
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string
import eu.timepit.refined.types.string._

trait ChannelsRepo[F[_]] {
  def createChannel(c: Channel): F[Unit]
  def getChannel(id: NonEmptyString): F[Option[Channel]]
  def listChannelsByTopic(topic: NonEmptyString, lastEvaluatedKey: Option[Int]): F[ListChannelsResponse]
}

object ChannelsRepo {

  case object ChannelNotFound
  case object InvalidLastEvaluatedKey extends Throwable

  case class ChannelPersistence(id: NonEmptyString, topic: NonEmptyString, subscriberCount: Int, channel: Channel)

  case class ChannelsRepoConfig(
      dynamoConfig: DynamoDBConfig,
      channelsTableName: NonEmptyString,
      channelsByTopicIndexName: NonEmptyString,
  ) {
    def buildRepository(): IO[ChannelsRepo[IO]] =
      dynamoConfig
        .buildClient()
        .map(c =>
          ChannelsRepo(this, AwsSdkInterpreter(c))
        )
  }

  def apply(config: ChannelsRepoConfig, interpreter: DynamoOpA ~> IO): ChannelsRepo[IO] =
    new ChannelsRepo[IO] {

      implicit val toDynamoValueChannel   = ToDynamoMap[ChannelPersistence]
      implicit val fromDynamoValueChannel = FromDynamoValue[ChannelPersistence]

      val channelsTable = Table[ChannelPersistence](config.channelsTableName.value, 'id)
      val topicsIndex   = channelsTable.globalSecondaryIndex(config.channelsByTopicIndexName.value, 'topic, 'subscriberCount)

      override def createChannel(c: Channel): IO[Unit] =
        channelsTable.put(ChannelPersistence(c.id, c.topic, 0, c)).eval(interpreter)

      override def getChannel(id: string.NonEmptyString): IO[Option[Channel]] =
        channelsTable.get(id).eval(interpreter).map(_.map(_.channel))

      override def listChannelsByTopic(tag: string.NonEmptyString, lastEvaluatedKey: Option[Int]): IO[ListChannelsResponse] =
        topicsIndex
          .query(tag, startAt = lastEvaluatedKey, descending = true)
          .eval(interpreter)
          .flatMap { r =>
            val key = r.lastEvaluatedKey.map(topicsIndex.parseKey) match {
              case Some(Right(k)) => IO.pure(Some(k))
              case None           => IO.pure(None)
              case Some(Left(e))  => IO.raiseError(e)
            }
            key.map { key =>
              ListChannelsResponse(r.results.map(_.channel), key.map(_._2))
            }
          }
    }
}
