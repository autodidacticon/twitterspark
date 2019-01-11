package io.moorhead.toptags.server

import io.moorhead.toptags.service.Tag
import org.scalatra._
import redis.clients.jedis.Jedis
import scala.collection.JavaConversions._

class TopTagsWebServer extends ScalatraServlet {
  lazy val db = new Jedis("127.0.0.1")
  get("/") {
    views.html.hello(getTopTags)
  }

  def getTopTags: Seq[Tag] = {
    db.zrevrangeByScoreWithScores("tags", 1000, 0, 0, 100)
      .map(t => Tag(t.getElement, t.getScore.toLong))
      .toSeq.sortBy(_.count).reverse
  }
}
