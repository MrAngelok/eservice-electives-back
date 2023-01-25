package edu.omsu.eservice.conf

import java.util

import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.config.Config
import edu.omsu.eservice.security.DasOAuth2Client
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer
import org.pac4j.core.context.WebContext
import org.pac4j.core.http.RelativeCallbackUrlResolver
import org.pac4j.oauth.profile.{JsonHelper, OAuth20Profile}

object Auth {

  case class Profile(personId: Option[Long], username: String, domain: String) extends OAuth20Profile

  class PersonAuthorizer extends ProfileAuthorizer[Profile] {
    override def isProfileAuthorized(context: WebContext, profile: Profile): Boolean = profile.personId.nonEmpty

    override def isAuthorized(context: WebContext, profiles: util.List[Profile]): Boolean =
      isAnyAuthorized(context, profiles)
  }

  class PersonExtractor(personIdByLoginResolver: (String) => Option[Long]) extends DasOAuth2Client.ProfileExtractor[Profile] {
    override def extract(data: JsonNode): Profile = {
      val username = Option.apply(JsonHelper.getElement(data, "username").asInstanceOf[String]) match {
        case Some(str) => str.toUpperCase
        case None => null
      }
      val domain = JsonHelper.getElement(data, "domain").asInstanceOf[String]
      // If person id was provided by DAS then use it, otherwise fetch from database by login
      val personIdOpt: Option[Long] = Option.apply(JsonHelper.getElement(data, "personId").asInstanceOf[Number])
      match {
        case Some(personIdNumber) => Some(personIdNumber.longValue())
        case None => personIdByLoginResolver(username)
      }
      Profile(personIdOpt, username, domain)
    }
  }


  def makeDasClient(config: Config, personIdByLoginResolver: (String) => Option[Long]): DasOAuth2Client[Profile] = {
    val dasServerUri = config.getString("das.server.uri")
    val dasDataUri = config.getString("das.data.server.uri")
    val dasClientId = config.getString("das.client.id")
    val dasSecret = config.getString("das.client.secret")
    val dasHost = config.getString("das.server.host")
    val scope = config.getString("das.server.scope")

    val extractor: PersonExtractor = new PersonExtractor(personIdByLoginResolver)
    val client = new DasOAuth2Client[Profile](dasServerUri, dasDataUri, scope, extractor, new PersonAuthorizer)

    client.setKey(dasClientId)
    client.setSecret(dasSecret)
    client.setCallbackUrlResolver(new RelativeCallbackUrlResolver)
    client
  }
}
