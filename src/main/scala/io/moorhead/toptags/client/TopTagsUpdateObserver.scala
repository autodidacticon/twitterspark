package io.moorhead.toptags.client

import com.typesafe.scalalogging.LazyLogging
import io.grpc.stub.StreamObserver
import io.moorhead.toptags.service.TagsUpdate

trait TopTagsUpdateObserver extends StreamObserver[TagsUpdate] with LazyLogging {
  override def onNext(value: TagsUpdate): Unit = logger.debug(value.tags.map(t => s"${t.tag} -> ${t.count}").mkString("\n"))

  override def onError(t: Throwable): Unit = logger.error(t.getMessage)

  override def onCompleted(): Unit = {}
}

object TopTagsUpdateObserver extends TopTagsUpdateObserver