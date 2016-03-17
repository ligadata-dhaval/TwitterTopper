package co.vine.ws.utility

import co.vine.ws.exceptions.SystemError
import co.vine.ws.vo.{ResponseBody, TweetStatus}
import com.github.scribejava.core.model.{OAuthRequest, Verb}
import play.api.libs.json.{JsObject, JsResultException, Json}

import scala.util.control.Exception._

/**
 * Domain specific language for twitter user list creation, addition of members and deletion
 *
 */
object TwitterDsl extends Logger {
  //Perform twitter oauth and receive the twitter service.
  val accessToken = OauthService.accessToken
  val service = OauthService.service
  var request: OAuthRequest = _

  /*
    Creates a new list for the authenticated user. Note that you can create up to 1000 lists per account.
   */
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

  /*
   Adds multiple members to a list, by specifying a comma-separated list of member ids or screen names. The authenticated user must own the list to be able to add members to it. Note that lists can’t have more than 5,000 members, and you are limited to adding up to 100 members to a list at a time with this method. Please note that there can be issues with lists that rapidly remove and add memberships. Take care when using these methods such that you are not too rapidly switching between removals and adds on the same list.
   @param screenNames List of users to be added
   @param listId Id of the list to which the users are to be added
  */
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

  /*
  display and page through a time-ordered list of statuses of several Twitter users
  @param listId Id of the list which has the users whose statuses are to be retrieved
  @param cursor When the response object has a non-null 'next_cursor' member, the value may be passed as the 'cursor' parameter in the next request to fetch the next page of results.
  @param count No of statuses to be retrieved
   */
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

  /*
    delete the users list
    @param listId The Id of the list to be deleted
     */
  def deleteUsersList(listId: String) {
    val deleteListQuery = "https://api.twitter.com/1.1/lists/destroy.json?list_id=" + listId
    request = new OAuthRequest(Verb.POST, deleteListQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4
    val response = request.send()
    logger.info("User list is deleted? " + response.isSuccessful)
    if (!response.isSuccessful) {
      logger.error("Error deleting the users list " + listId)
      throw new SystemError("Error deleting the users list")
    }
  }
}
