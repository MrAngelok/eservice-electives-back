package edu.omsu.eservice.conf

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.thread.{QueuedThreadPool, ThreadPool}
import spark.embeddedserver.jetty.{EmbeddedJettyServer, JettyHandler, JettyServerFactory}
import spark.embeddedserver.{EmbeddedServer, EmbeddedServerFactory}
import spark.http.matching.MatcherFilter
import spark.route.Routes
import spark.staticfiles.StaticFilesConfiguration

/**
  * Created by voroshilovvv on 18.01.2019.
  */
class EserviceJettyServerFactory extends JettyServerFactory {
  override def create(maxThreads: Int, minThreads: Int, threadTimeoutMillis: Int): Server = {
    val server: Server =
      if (maxThreads > 0) {
        val max: Int = if (maxThreads > 0) maxThreads else 200
        val min: Int = if (minThreads > 0) minThreads else 8

        val idleTimeout: Int = if (threadTimeoutMillis > 0) threadTimeoutMillis else 60
        new Server(new QueuedThreadPool(max, min, idleTimeout))
      } else {
        new Server()
      }

    server
  }

  override def create(threadPool: ThreadPool): Server = if (threadPool != null) new Server(threadPool) else new Server
}


class EserviceEmbeddedServerFactory(val appRoot: String) extends EmbeddedServerFactory {
  override def create(routeMatcher: Routes, staticFilesConfiguration: StaticFilesConfiguration, hasMultipleHandler: Boolean): EmbeddedServer = {
    val matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler)
    matcherFilter.init(null)

    val handler = new JettyHandler(matcherFilter)
    val cookieConfig = handler.getSessionCookieConfig
    cookieConfig.setPath(appRoot)

    new EmbeddedJettyServer(new EserviceJettyServerFactory, handler)
  }
}
