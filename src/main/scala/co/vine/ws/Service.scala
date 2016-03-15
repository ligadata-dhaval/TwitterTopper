package co.vine.ws

import co.vine.ws.exceptions.SystemError
import co.vine.ws.vo.{ResponseBody, TweetStatus}
import com.github.scribejava.core.oauth._
import scala.util.control.Exception.catching
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.exceptions.OAuthException
import com.github.scribejava.core.model._
import play.api.libs.json._
/**
 * Created by dhavalkolapkar on 3/9/16.
 */


object Service {
  var service: OAuth10aService=_
  var accessToken: OAuth1AccessToken=_
  val serviceCatch = catching(classOf[OAuthException]).withApply(e => throw new SystemError(e))
  serviceCatch.opt{
    service= new ServiceBuilder()
      .apiKey("jK6k4cbCfO191K2tezqm1eZqQ")
      .apiSecret("YkqNYCjkFUHqQLn7Kh5IxpgFJ8hqIrQcZsWPil4PWh6moYbQhg")
      .build(TwitterApi.Authenticate.instance())
    accessToken = new OAuth1AccessToken("220347935-yNr3XU7UV5JcRWxKRZVJLgHWesjVIJdtYap5n7KL", "11JtXVWzJd4GnCk84dtxEA06x4QkTInNy6W25OWSNhRTZ")

  }
  def getAuthenticationDetails = (service, accessToken)

}

class Service {
  var logger: org.apache.logging.log4j.Logger = org.apache.logging.log4j.LogManager.getLogger(this.getClass)
  var max_id: String = ""
  var request: OAuthRequest = _
  var responseBody: String = _
  var response: Response = _
  var json: JsValue = _
  var jsUserValue: JsValue = _

  def getStatuses(screenNames: String, count: Int, cursor: String): JsValue = {
    val JsonCatch = catching(classOf[JsResultException], classOf[NullPointerException]).withApply(e => throw new SystemError(e.getMessage))
    JsonCatch.opt{
      val (service, accessToken) = Service.getAuthenticationDetails
      request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/lists/create.json?name=" + System.currentTimeMillis().toString + "&mode=public&description=For%20life", service)
      service.signRequest(accessToken, request) // the access token from step 4
      response = request.send()
      responseBody = response.getBody
      json = Json.parse(responseBody.toString)
      val slug = (json \ "slug").as[String]
      val listId = (json \ "id_str").as[String]
      logger.debug("Creation of user list,"+listId+", with slug "+slug)
      if (response.isSuccessful) {
        val createMembersQuery = "https://api.twitter.com/1.1/lists/members/create_all.json?screen_name=" + screenNames + "&list_id=" + listId
        request = new OAuthRequest(Verb.POST, createMembersQuery, service)
        service.signRequest(accessToken, request)
        response = request.send()
        responseBody = response.getBody
        json = Json.parse(responseBody.toString)
        logger.info("Add users to the list "+json)
        if (response.isSuccessful) {
          var getStatusesQuery: String = ""
          if (cursor == null){
            logger.info("User did not provide a cursor.")
            getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug=" + slug.toString + "&owner_screen_name=PunyacheRau&count=" + count + "&include_entities=true"
          }
          else{
            logger.info("User provided a cursor "+cursor)
            getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug=" + slug.toString + "&owner_screen_name=PunyacheRau&count=" + count + "&include_entities=true" + "&max_id=" + cursor
          }
          request = new OAuthRequest(Verb.GET, getStatusesQuery, service)
          service.signRequest(accessToken, request)
          response = request.send()
          responseBody = response.getBody
          json = Json.parse(responseBody)
          val userListStatuses = (json).as[List[JsObject]]
          val userListShortStatuses = new Array[String](userListStatuses.size - 1)
          var countOfUserListStatuses = 0
          var id_str: String = null
          for (userInfo <- userListStatuses) {
            val json = Json.parse(userInfo.toString())
            val userName = (json \ "user" \ "name").result.as[String]
           //Update the hashtags, mentions and urls
            val text = addExpandedURLs((json \ "entities" \ "urls").as[List[JsObject]], addMentionsURL((json \ "entities" \ "user_mentions").as[List[JsObject]], addHashTagURL((json \ "entities" \ "hashtags").as[List[JsObject]], (json \ "text").result.as[String])))

            implicit val userImplicitWrites = Json.writes[TweetStatus]
            val status = (Json.toJson(new TweetStatus(userName, text))).toString()
            if (countOfUserListStatuses == (userListStatuses.size - 1)) {
              id_str = (json \ "id_str").result.as[String]
            } else {
              userListShortStatuses(countOfUserListStatuses) = status
              countOfUserListStatuses = countOfUserListStatuses + 1
            }
          }

          implicit val userImplicitWrites = Json.writes[ResponseBody]
          jsUserValue = Json.toJson(new ResponseBody(id_str, userListShortStatuses))

          if (response.isSuccessful) {
            //delete the user list
            val deleteListQuery = "https://api.twitter.com/1.1/lists/destroy.json?owner_screen_name=PunyacheRau&slug=" + slug
            request = new OAuthRequest(Verb.POST, deleteListQuery, service)
            service.signRequest(accessToken, request) // the access token from step 4
              response= request.send()
            logger.info("User list "+slug+" is deleted? "+response.isSuccessful)
          }
        } else {
          logger.error("Member creation failed!")
          throw new SystemError("Member Creation Failed!")
        }
      } else {
        logger.error("List creation failed")
        throw new SystemError("List Creation Failed!")
      }
    }
    jsUserValue
    }

  def addHashTagURL(hashtags: List[JsObject], text: String): String = {
    var modifiedText: String = text
    for (hashtag <- hashtags) {
      val json = Json.parse(hashtag.toString())
      val hashtagData = (json \ "text").result.as[String]
      val url = "https://twitter.com/hashtag/" + hashtagData
      val replace = "<a href= " + url + ">#" + hashtagData + "</a>"
      val regex = ("#" + hashtagData).r
      val tempText = regex.replaceAllIn(modifiedText, replace)
      modifiedText = tempText
    }
    modifiedText
  }

  def addMentionsURL(mentions: List[JsObject], text: String): String = {
    var modifiedText: String = text
    for (mention <- mentions) {
      val json = Json.parse(mention.toString())
      val mentionedScreenName = (json \ "screen_name").result.as[String]
      val url = "https://twitter.com/" + mentionedScreenName
      val replace = "<a href=" + url + ">@" + mentionedScreenName + "</a>"
      val regex = ("@" + mentionedScreenName).r
      val tempText = regex.replaceAllIn(modifiedText, replace)
      modifiedText = tempText
    }
    modifiedText
  }

  def addExpandedURLs(urls: List[JsObject], text: String): String = {
    var modifiedText: String = text
    for (url <- urls) {
      val json = Json.parse(url.toString())
      val replace = "<a href=" + (json \ "expanded_url").result.as[String] + ">" + (json \ "display_url").result.as[String] + "</a>"
      val regex = ((json \ "url").result.as[String]).r
      val tempText = regex.replaceAllIn(modifiedText, replace)
      modifiedText = tempText
    }
    modifiedText
  }
}