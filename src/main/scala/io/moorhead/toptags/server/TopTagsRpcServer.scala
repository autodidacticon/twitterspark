package io.moorhead.toptags.server

import com.typesafe.scalalogging.LazyLogging
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import io.moorhead.toptags.service.{Tag, TagsUpdate, TopTagsGrpc}
import redis.clients.jedis.Jedis

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object TopTagsRpcServer extends App {
  val server = ServerBuilder.forPort(8088).addService(TopTagsGrpc.bindService(TopTagsService, ExecutionContext.global)).build()

  server.start()
  server.awaitTermination()
}

object TopTagsService extends TopTagsGrpc.TopTags with LazyLogging {

  override def tagsRoute(responseObserver: StreamObserver[TagsUpdate]): StreamObserver[Tag] = new StreamObserver[Tag] {
    val tagMap = mutable.Map.empty[String, Long]
    override def onNext(value: Tag): Unit = {
      val ct = tagMap.getOrElse(value.tag, 0L)
      tagMap.put(value.tag, ct + 1)
    }

    override def onError(t: Throwable): Unit = logger.error(t.getMessage)

    override def onCompleted(): Unit = {
      lazy val db = new Jedis("127.0.0.1")
      val tagsUpdate = TagsUpdate(tagMap.map(t => Tag(t._1, t._2)).toSeq)
      tagsUpdate.tags.foreach(t => db.zincrby("tags", t.count, t.tag))
      logger.debug(tagsUpdate.tags.mkString("\n"))
      responseObserver.onNext(tagsUpdate)
      responseObserver.onCompleted()
    }
  }
}
