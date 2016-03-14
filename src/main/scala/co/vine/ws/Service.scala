package co.vine.ws

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model._
import play.api.libs.json._

/**
 * Created by dhavalkolapkar on 3/9/16.
 */

case class StatusResponse(@JsonProperty("text") status: String, @JsonProperty("id") user: String){

}

object Service {

  lazy val service = new ServiceBuilder()
    .apiKey("jK6k4cbCfO191K2tezqm1eZqQ")
    .apiSecret("YkqNYCjkFUHqQLn7Kh5IxpgFJ8hqIrQcZsWPil4PWh6moYbQhg")
    .build(TwitterApi.Authenticate.instance())
  lazy val accessToken = new OAuth1AccessToken("220347935-yNr3XU7UV5JcRWxKRZVJLgHWesjVIJdtYap5n7KL", "11JtXVWzJd4GnCk84dtxEA06x4QkTInNy6W25OWSNhRTZ")

  def getAuthenticationDetails = (service, accessToken)
}

class Service {
  var request: OAuthRequest = _
  var response: String = _
  var json: JsValue =_

  def getStatuses(screenNames: String, count: Int): Unit = {

    val (service, accessToken) = Service.getAuthenticationDetails
    //create users list
    request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/lists/create.json?name=HotAss&mode=public&description=For%20life", service)
    service.signRequest(accessToken, request) // the access token from step 4
    response = request.send().getBody
    json =  Json.parse(response.toString)
    val slug = (json \ "slug" ).as[String]
    val listId= (json \ "id_str" ).as[String]
    //println("slug is: "+slug)

    //add screen_names to the users list
    var createMembersQuery = "https://api.twitter.com/1.1/lists/members/create_all.json?screen_name=" + screenNames + "&list_id="+listId
    request = new OAuthRequest(Verb.POST, createMembersQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4
    response = request.send().getBody
    json=  Json.parse(response.toString)
    /*
    var gson = new Gson()

    var navigationArray = gson.fromJson(response,classOf[TweetResponse])

    var collectionType = new TypeToken[List[TweetResponse]](){}.getType
    var navigation: List[TweetResponse] = gson.fromJson(response, collectionType)
    navigation.foreach(f=> println(f))
    //println(response.getBody())*/

    //get statuses of the query
    var getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug="+slug.toString+"&owner_screen_name=PunyacheRau&count=5&include_entities=false"
    request = new OAuthRequest(Verb.GET, getStatusesQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4

    response = request.send().getBody
     json=Json.parse(response)
    var userInfos=(json).as[List[JsObject]]
    for(userInfo<-userInfos){
      var json=Json.parse(userInfo.toString())
      var text=(json \ "text")
      var username=(json \ "user" \ "name")
      println("user: "+username +" status: "+text)
    }
  /*val userInfo=  Json.parse(response).map(_.as[String]).toList
  userInfo.foreach(println)
*/


    //delete the user list
    var deleteListQuery="https://api.twitter.com/1.1/lists/destroy.json?owner_screen_name=PunyacheRau&slug="+slug
    request = new OAuthRequest(Verb.POST, deleteListQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4
    response = request.send().getBody
   // println(response.getBody())
  }

}

