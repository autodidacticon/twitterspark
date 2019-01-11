package io.moorhead.twitterspark.spark

import com.typesafe.scalalogging.LazyLogging
import io.moorhead.twitterspark.twitter.TwitterStream
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import twitter4j.{Status, TwitterStreamFactory}

class TweetReceiver(terms: Seq[String], override val storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER) extends Receiver[Status](storageLevel) with LazyLogging with TwitterStream {
  lazy val stream = TwitterStreamFactory.getSingleton.addListener(this.listener)

  override def onStart(): Unit = {
    stream.filter(terms: _*)
  }

  override def onStop(): Unit = {}
}
