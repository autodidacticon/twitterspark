package io.moorhead.twitterspark

import com.typesafe.scalalogging.LazyLogging
import io.grpc.ManagedChannelBuilder
import io.moorhead.toptags.client.TopTagsUpdateObserver
import io.moorhead.toptags.service.{Tag, TopTagsGrpc}
import io.moorhead.twitterspark.spark.TweetReceiver
import io.moorhead.twitterspark.twitter.TwitterStream
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Duration, StreamingContext}
import twitter4j.Status

import scala.util.{Failure, Success, Try}

object TwitterSpark extends App with LazyLogging {

  val conf = new SparkConf()
  conf.setMaster("local[8]")
  conf.registerKryoClasses(Array(classOf[TwitterStream]))
  val ss = SparkSession.builder().config(conf).getOrCreate()

  import ss.implicits._

  val sc = new StreamingContext(ss.sparkContext, Duration(1000))

  val stub = TopTagsGrpc.stub(ManagedChannelBuilder.forAddress("127.0.0.1", 8088).usePlaintext().build)
  val statusStream = sc.receiverStream[Status](new TweetReceiver(args))
  statusStream
    .filter(!_.getHashtagEntities.isEmpty)
    .flatMap(_.getHashtagEntities.map(_.getText))
    .countByValue()
    .map { case (tag, ct) => Tag(tag, ct) }
    .foreachRDD(rdd => {
      rdd.toDS().foreachPartition { msgs =>
        val requestObserver = stub.tagsRoute(TopTagsUpdateObserver)
        msgs.foreach { msg =>
          Try {
            requestObserver.onNext(msg)
          } match {
            case Success(_) => logger.info(s"${msg.tag}: ${msg.count}")
            case Failure(e) => {
              logger.error(e.getMessage)
            }
          }
        }
        requestObserver.onCompleted()
        Thread.sleep(100)
      }

    })

  sc.start()
  sc.awaitTermination()
}

