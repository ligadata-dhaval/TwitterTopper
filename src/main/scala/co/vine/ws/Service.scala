package co.vine.ws

import _root_.co.vine.ws.exceptions.SystemError
import co.vine.ws
import com.github.scribejava.core.oauth._
import _root_.co.vine.ws.co.vine.ws.vo.{ResponseBody, TweetStatus, Response}
import scala.util.control.Exception.catching
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.exceptions.OAuthException
import com.github.scribejava.core.model._
import com.google.gson.Gson
import play.api.libs.json._

import scala.beans.BeanProperty
import scala.collection.mutable.ListBuffer

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
  var max_id: String = ""
  var request: OAuthRequest = _
  var responseBody: String = _
  var response: Response = _
  var json: JsValue = _
  var jsUserValue: JsValue = _

  def getStatuses(screenNames: String, count: Int, cursorTemp: String): JsValue = {
    val JsonCatch = catching(classOf[JsResultException], classOf[NullPointerException]).withApply(e => throw new SystemError(e.getMessage))
      var cursor = cursorTemp
    JsonCatch.opt{
      val (service, accessToken) = Service.getAuthenticationDetails
      request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/lists/create.json?name=" + System.currentTimeMillis().toString + "&mode=public&description=For%20life", service)
      service.signRequest(accessToken, request) // the access token from step 4
      response = request.send()
      responseBody = response.getBody
      json = Json.parse(responseBody.toString)
      val slug = (json \ "slug").as[String]
      val listId = (json \ "id_str").as[String]
      if (response.isSuccessful) {
        val createMembersQuery = "https://api.twitter.com/1.1/lists/members/create_all.json?screen_name=" + screenNames + "&list_id=" + listId
        request = new OAuthRequest(Verb.POST, createMembersQuery, service)
        service.signRequest(accessToken, request) // the access token from step 4
        response = request.send()
        responseBody = response.getBody
        json = Json.parse(responseBody.toString)

        if (response.isSuccessful) {
          var getStatusesQuery: String = ""
          if (cursor == null)
            getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug=" + slug.toString + "&owner_screen_name=PunyacheRau&count=" + count + "&include_entities=true"
          else
            getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug=" + slug.toString + "&owner_screen_name=PunyacheRau&count=" + count + "&include_entities=true" + "&max_id=" + cursor
          request = new OAuthRequest(Verb.GET, getStatusesQuery, service)
          service.signRequest(accessToken, request) // the access token from step 4
          response = request.send()
          responseBody = response.getBody
          json = Json.parse(responseBody)
          var userInfos = (json).as[List[JsObject]]
          var statuses = new Array[String](userInfos.size - 1)
          var i = 0
          var id_str: String = null
          for (userInfo <- userInfos) {
            var json = Json.parse(userInfo.toString())
            var text = (json \ "text").result.as[String]
            var username = (json \ "user" \ "name").result.as[String]
            //hashtags
            var hashtags = (json \ "entities" \ "hashtags").as[List[JsObject]]
            var temp = addHashTagURL(hashtags, text)
            //mentions
            var mentions = (json \ "entities" \ "user_mentions").as[List[JsObject]]
            var temp2 = addMentionsURL(mentions, temp)
            //urls
            var urls = (json \ "entities" \ "urls").as[List[JsObject]]
            var temp3: String = addExpandedURLs(urls, temp2)

            implicit val userImplicitWrites = Json.writes[TweetStatus]
            val jsUserValue = Json.toJson(new TweetStatus(username, temp3))
            var status = (jsUserValue).toString()
            if (i == (userInfos.size - 1)) {
              id_str = (json \ "id_str").result.as[String]
            } else {
              statuses(i) = status
              i = i + 1
            }
          }

          implicit val userImplicitWrites = Json.writes[ResponseBody]
          jsUserValue = Json.toJson(new ResponseBody(id_str, statuses))

          if (response.isSuccessful) {
            //delete the user list
            var deleteListQuery = "https://api.twitter.com/1.1/lists/destroy.json?owner_screen_name=PunyacheRau&slug=" + slug
            request = new OAuthRequest(Verb.POST, deleteListQuery, service)
            service.signRequest(accessToken, request) // the access token from step 4
          }
        } else {
          throw new SystemError("Member Creation Failed!")        }
      } else {
        //println("List creation failed")
        throw new SystemError("List Creation Failed!")
      }
    }
    jsUserValue
    }

  def addHashTagURL(hashtags: List[JsObject], text: String): String = {
    var temp: String = text
    var temp1: String = ""
    for (hashtag <- hashtags) {
      val json = Json.parse(hashtag.toString())
      val hashtagdata = (json \ "text").result.as[String]
      val url = "https://twitter.com/hashtag/" + hashtagdata
      val replace = "<a href= " + url + ">#" + hashtagdata + "</a>"
      val regex = ("#" + hashtagdata).r
      val newText = regex.replaceAllIn(temp, replace)
      temp = newText
    }
    temp
  }

  def addMentionsURL(mentions: List[JsObject], text: String): String = {
    var temp: String = text
    for (mention <- mentions) {
      val json = Json.parse(mention.toString())
      val mentionedScreenName = (json \ "screen_name").result.as[String]
      val url = "https://twitter.com/" + mentionedScreenName
      val replace = "<a href=" + url + ">@" + mentionedScreenName + "</a>"
      val regex = ("@" + mentionedScreenName).r
      val newText = regex.replaceAllIn(temp, replace)
      temp = newText
    }
    temp
  }

  def addExpandedURLs(urls: List[JsObject], text: String): String = {
    var temp: String = text
    for (otherurl <- urls) {
      val json = Json.parse(otherurl.toString())
      val expandedUrl = (json \ "expanded_url").result.as[String]
      var displayUrl = (json \ "display_url").result.as[String]
      var textUrl = (json \ "url").result.as[String]
      val replace = "<a href=" + expandedUrl + ">" + displayUrl + "</a>"
      val regex = textUrl.r
      val newText = regex.replaceAllIn(temp, replace)
      temp = newText
    }
    temp
  }

}