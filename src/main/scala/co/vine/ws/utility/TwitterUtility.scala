package co.vine.ws.utility

import play.api.libs.json.{Json, JsObject}

/**
 * Created by dhavalkolapkar on 3/15/16.
 */
object TwitterUtility {

  def addHashTagURL(hashtags: List[JsObject], text: String): String = {
    var modifiedText: String = text
    for (hashtag <- hashtags) {
      val json = Json.parse(hashtag.toString())
      val hashtagData = (json \ "text").result.as[String]
      val url = "https://twitter.com/hashtag/" + hashtagData
      val replace = "<a href=" + url + ">#" + hashtagData + "</a>"
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
