package io.moorhead.twitterspark.twitter

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.streaming.receiver.Receiver
import twitter4j.{StallWarning, Status, StatusDeletionNotice, StatusListener}

trait TwitterStream {
  self: Receiver[Status] with LazyLogging =>

  val listener: StatusListener = new StatusListener() {
    def onStatus(status: Status): Unit = self.store(status)

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
    }

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
    }

    def onException(ex: Exception): Unit = logger.error(ex.getMessage)

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

    override def onStallWarning(warning: StallWarning): Unit = {}
  }
}

