package co.vine.ws.utility

import co.vine.ws.exceptions.SystemError
import co.vine.ws.vo.{ResponseBody, TweetStatus}
import com.github.scribejava.core.model.{Response, Verb, OAuthRequest}
import play.api.libs.json.{JsObject, JsValue, Json, JsResultException}

import scala.util.control.Exception._

/**
 * Created by dhavalkolapkar on 3/16/16.
 *
 */
object TwitterDsl extends Logger {
  //Perform twitter oauth and receive the twitter service.
  val accessToken = OauthService.accessToken
  val service = OauthService.service
  var request: OAuthRequest = _

  def createUsersList(): String = {
    var listId: String = ""
    val JsonCatch = catching(classOf[JsResultException], classOf[NullPointerException]).withApply(e => throw new SystemError(e.getMessage))
    JsonCatch.opt {
      logger.debug("Creating a user list.")
      request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/lists/create.json?name=" + System.currentTimeMillis().toString + "&mode=public&description=For%20life", service)
      service.signRequest(accessToken, request) // the access token from step 4
      val response = request.send()
      if (!response.isSuccessful) {
        logger.error("Creation of user list failed!")
        throw new SystemError("Connection error: Creation of user list failed.")
      }
      val json = Json.parse(response.getBody)
      val slug = (json \ "slug").as[String]
      listId = (json \ "id_str").as[String]
      logger.debug("Creation of user list," + listId + ", with slug " + slug)
    }
    (listId)
  }

  def addMembersToList(screenNames: String, listId: String): String = {
    val JsonCatch = catching(classOf[JsResultException], classOf[NullPointerException]).withApply(e => throw new SystemError(e.getMessage))
    JsonCatch.opt {
      logger.debug("Adding members to the user list.")
      val createMembersQuery = "https://api.twitter.com/1.1/lists/members/create_all.json?screen_name=" + screenNames + "&list_id=" + listId
      request = new OAuthRequest(Verb.POST, createMembersQuery, service)
      service.signRequest(accessToken, request)
      val response = request.send()
      if (!response.isSuccessful) {
        logger.error("Addition of members to the user list failed!")
        throw new SystemError("Connection error: Addition of members to the user list failed. Please retry.")
      }
      val json = Json.parse(response.getBody)
      logger.info("Add users to the list " + json)
    }
    listId
  }

  def getUserListStatuses(listId: String, cursor: String, count: Int): String = {
    var userStatuses: String = ""
    val JsonCatch = catching(classOf[JsResultException], classOf[NullPointerException]).withApply(e => throw new SystemError(e.getMessage))
    JsonCatch.opt {
      logger.debug("Retrieving statuses of members of    the user list.")
      var getStatusesQuery: String = ""
      if (cursor == null) {
        logger.info("User did not provide a cursor.")
        getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?list_id=" + listId + "&count=" + count + "&include_entities=true"
      }
      else {
        logger.info("User provided a cursor " + cursor)
        getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?list_id=" + listId + "&count=" + count + "&include_entities=true" + "&max_id=" + cursor
      }
      request = new OAuthRequest(Verb.GET, getStatusesQuery, service)
      service.signRequest(accessToken, request)
      val response = request.send()
      if (!response.isSuccessful) {
        logger.error("Connection error: Unable to retrieve statuses at this time! Please try again.")
        throw new SystemError("Connection error: Unable to retrieve statuses at this time! Please try again.")
      }

      val json = Json.parse(response.getBody)
      val userListStatuses = (json).as[List[JsObject]]
      if (userListStatuses.size < 0) {
        logger.error("No statuses found for the requested users!")
        throw new SystemError("No statuses found for the requested users!")
      }
      val userListShortStatuses = new Array[String](userListStatuses.size - 1)
      var countOfUserListStatuses = 0
      var id_str: String = null
      for (userInfo <- userListStatuses) {
        val json = Json.parse(userInfo.toString())
        val userName = (json \ "user" \ "name").result.as[String]

        //Update the hashtags, mentions and urls
        val text = TwitterUtility.addExpandedURLs((json \ "entities" \ "urls").as[List[JsObject]], TwitterUtility.addMentionsURL((json \ "entities" \ "user_mentions").as[List[JsObject]], TwitterUtility.addHashTagURL((json \ "entities" \ "hashtags").as[List[JsObject]], (json \ "text").result.as[String])))
        implicit val userImplicitWrites = Json.writes[TweetStatus]
        val status = (Json.toJson(new TweetStatus(userName, text))).toString()
        if (countOfUserListStatuses == (userListStatuses.size - 1)) {
          id_str = (json \ "id_str").result.as[String]
        } else {
          userListShortStatuses(countOfUserListStatuses) = status
          countOfUserListStatuses = countOfUserListStatuses + 1
        }
      }
      deleteUsersList(listId)
      implicit val userImplicitWrites = Json.writes[ResponseBody]
      userStatuses = Json.toJson(new ResponseBody(id_str, userListShortStatuses)).toString
    }
    userStatuses
  }

   def deleteUsersList(listId: String) {
    val deleteListQuery = "https://api.twitter.com/1.1/lists/destroy.json?list_id=" + listId
    request = new OAuthRequest(Verb.POST, deleteListQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4
    val response = request.send()
    logger.info("User list is deleted? " + response.isSuccessful)
    if(!response.isSuccessful){
      logger.error("Error deleting the users list "+listId)
    throw new SystemError("Error deleting the users list")
    }
   }
}
