package edu.omsu.eservice.conf

import com.typesafe.config.Config
import edu.omsu.eservice.conf.Auth.Profile
import edu.omsu.eservice.controllers.JsonResponseTransformer
import edu.omsu.eservice.security.{DasOAuth2Client, DasSparkIntegration}
import org.pac4j.core.profile.ProfileManager
import org.pac4j.sparkjava.SparkWebContext
import org.slf4j.LoggerFactory
import spark._
import spark.embeddedserver.EmbeddedServers

class Api(config: Config, client: DasOAuth2Client[Profile]) {
  private val logger = LoggerFactory.getLogger(classOf[Api])
  private val appRoot = if (config.hasPath("app.root")) config.getString("app.root") else ""

  EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EserviceEmbeddedServerFactory(appRoot))

  if (config.hasPath("app.port"))
    Spark.port(config.getInt("app.port"))

  val appUrl: String = config.getString("app.url")
  val dasServerUri: String = config.getString("das.server.uri")
  val dasHost: String = config.getString("das.server.host")
  val jsonTransformer = new JsonResponseTransformer
  val sessionLifetimeSec: Int = config.getInt("app.session_lifetime_sec")
  DasSparkIntegration.init(appRoot, appUrl, dasHost, dasServerUri, client)

  Spark.exception(classOf[Exception], new ExceptionHandler[Exception] {
    override def handle(ex: Exception, request: Request, response: Response): Unit = {
      val context = new SparkWebContext(request, response)
      val manager = new ProfileManager[Profile](context)
      if (!manager.isAuthenticated) {
        logger.error("[" + request.requestMethod() + " " + request.uri() + "] " + ex.getMessage, ex)
      } else {
        val profile: Profile = manager.get(true).get()
        profile.personId match {
          case Some(personId) => logger.error(profile.username + "(" + personId + ") [" + request.requestMethod() + " " + request.uri() + "] " + ex.getMessage, ex)
          case None => logger.error(profile.username + " [" + request.requestMethod() + " " + request.uri() + "] " + ex.getMessage, ex)
        }
      }
    }
  })


  def authorizedGet(path: String, fn: (String, Long, Request, Response) => AnyRef) = {
    DasSparkIntegration.enableSecurityFilter(appRoot + path)
    get(path, fn)
  }

  def get(path: String, fn: (String, Long, Request, Response) => AnyRef) =
    Spark.get(appRoot + path, new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)

        //FOR production to UNCOMMENT !!!
        if (!manager.isAuthenticated) {
          Spark.halt(401)
          return null
        }
        // At least one non anonymous profile exists
        val profile: Profile = manager.get(true).get()

        request.session().maxInactiveInterval(sessionLifetimeSec)
        fn(profile.username, profile.personId.get, request, response)

        //FOR production to COMMENT !!!
        //        fn("OVERLN2@GMAIL.COM", 128947, request, response)
      }
    }, jsonTransformer)

  def post(path: String, accessType: String, fn: (String, Long, Request, Response) => AnyRef) =
    Spark.post(appRoot + path, accessType, new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager: ProfileManager[Profile] = new ProfileManager[Profile](context)

        //FOR proda ction to UNCOMMENT !!!
        if (!manager.isAuthenticated) {
          Spark.halt(401)
          return null
        }
        // At least one non anonymous profile exists
        val profile: Profile = manager.get(true).get()

        request.session().maxInactiveInterval(sessionLifetimeSec)
        fn(profile.username, profile.personId.get, request, response)

        //FOR production to COMMENT !!!
//        fn("OVERLN2@GMAIL.COM", 128947, request, response)
      }
    }, jsonTransformer)

  def put(path: String, accessType: String, fn: (String, Long, Request, Response) => AnyRef) =
    Spark.put(appRoot + path, accessType, new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager: ProfileManager[Profile] = new ProfileManager[Profile](context)

        //FOR proda ction to UNCOMMENT !!!
        if (!manager.isAuthenticated) {
          Spark.halt(401)
          return null
        }
        // At least one non anonymous profile exists
        val profile: Profile = manager.get(true).get()

        request.session().maxInactiveInterval(sessionLifetimeSec)
        fn(profile.username, profile.personId.get, request, response)

        //FOR production to COMMENT !!!
        //        fn("OVERLN2@GMAIL.COM",  128947, request, response)
      }
    }, jsonTransformer)

  def delete(path: String, fn: (String, Long, Request, Response) => AnyRef) =
    Spark.delete(appRoot + path, new Route {
      override def handle(request: Request, response: Response): AnyRef = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)

        //FOR production to UNCOMMENT !!!
        if (!manager.isAuthenticated) {
          Spark.halt(401)
          return null
        }
        // At least one non anonymous profile exists
        val profile: Profile = manager.get(true).get()

        request.session().maxInactiveInterval(sessionLifetimeSec)
        fn(profile.username, profile.personId.get, request, response)

        //FOR production to COMMENT !!!
        //        fn("OVERLN2@GMAIL.COM",  128947, request, response)
      }
    }, jsonTransformer)
}