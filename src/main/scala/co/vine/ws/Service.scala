package co.vine.ws

import java.util

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
/*


case class HashTags(@JsonProperty("text") hashtag: JsString, @JsonProperty("indices") indices: Seq[JsString]){}
object HashTags{
  implicit val jsonFormat: Format[HashTags] = Json.format[HashTags]
}
*/



object Service {

   val service = new ServiceBuilder()
    .apiKey("jK6k4cbCfO191K2tezqm1eZqQ")
    .apiSecret("YkqNYCjkFUHqQLn7Kh5IxpgFJ8hqIrQcZsWPil4PWh6moYbQhg")
    .build(TwitterApi.Authenticate.instance())
   val accessToken = new OAuth1AccessToken("220347935-yNr3XU7UV5JcRWxKRZVJLgHWesjVIJdtYap5n7KL", "11JtXVWzJd4GnCk84dtxEA06x4QkTInNy6W25OWSNhRTZ")

  def getAuthenticationDetails = (service, accessToken)
}

class Service {
  var request: OAuthRequest = _
  var response: String = _
  var json: JsValue =_

  def getStatuses(screenNames: String, count: Int): Unit = {
   // println("Screen names are: "+screenNames+" and count is: "+count)
    val (service, accessToken) = Service.getAuthenticationDetails
    //create users list
    request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/lists/create.json?name=Kanika&mode=public&description=For%20life", service)
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
    var getStatusesQuery = "https://api.twitter.com/1.1/lists/statuses.json?slug="+slug.toString+"&owner_screen_name=PunyacheRau&count="+count+"&include_entities=true"
    request = new OAuthRequest(Verb.GET, getStatusesQuery, service)
    service.signRequest(accessToken, request) // the access token from step 4

    response = request.send().getBody
     json=Json.parse(response)
    var userInfos=(json).as[List[JsObject]]
    for(userInfo<-userInfos){
      var json=Json.parse(userInfo.toString())
      var text=(json \ "text").result.as[String]
      var username=(json \ "user" \ "name").result.as[String]
      //hashtags
      var hashtags=(json \ "entities" \ "hashtags").as[List[JsObject]]
      var temp=addHashTagURL(hashtags,text)
      //mentions
      var mentions=(json \ "entities" \ "user_mentions").as[List[JsObject]]
         var temp2=addMentionsURL(mentions,temp)

      println("URL: "+temp2)
      //println("user: "+username.toString +" status: "+text.toString+ " hastags: "+hashtags.toString())

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
    //println("Process ends!")
  }

  def addHashTagURL(hashtags: List[JsObject],text: String): String ={
   //println("Adding hashtag url")
    var temp : String=text
    var temp1 : String=""
      for(hashtag<-hashtags){
        val json=Json.parse(hashtag.toString())
      //x println(json)
        val hashtagdata=(json \ "text").result.as[String]
       /*// println("bawa: " + hashtagdata)
        val index1=(json \ "indices").result.get(0).as[Int]
        val index2=(json \ "indices").result.get(1).as[Int]

        val url="https://twitter.com/hashtag/"+hashtagdata
        val anchorStart="<a href="+url+">"
       /* var startLoc=hashtag.indices(0).result.as[String]
        var endLoc=hashtag.indices(1).result.as[String]*/

        var (fst, snd) = temp.splitAt(index1)
        temp=""
        temp=fst +anchorStart+ snd
        val (first, second) = temp.splitAt(index2)
        temp=""
        temp=first +"</a>"+ second*/
        val url="https://twitter.com/hashtag/"+hashtagdata
        val replace="<a href= "+url+">#"+hashtagdata+"</a>"
        val regex = ("#"+hashtagdata).r
        val newText = regex.replaceAllIn(temp, replace)
        temp=newText
      }
    temp
  }

  def addMentionsURL(mentions: List[JsObject],text: String): String ={
    var temp : String=text
      for(mention<-mentions){
        val json=Json.parse(mention.toString())
        val mentionedScreenName=(json \ "screen_name").result.as[String]
        val url="https://twitter.com/"+mentionedScreenName
        val replace="<a href="+url+">@"+mentionedScreenName+"</a>"
        val regex = ("@"+mentionedScreenName).r
        val newText = regex.replaceAllIn(temp, replace)
        temp=newText
      }
    temp
  }


}

