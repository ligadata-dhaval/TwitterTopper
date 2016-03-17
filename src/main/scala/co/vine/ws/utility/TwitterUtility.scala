package co.vine.ws.utility

import play.api.libs.json.{JsObject, Json}

/**
 * Utility for transforming the hashtags, mentions and urls to expanded url
 */
object TwitterUtility {
  /*
    hashtags should link to the hashtag page, e.g. https://twitter.com/hashtag/ Tag
    @param hashtags list of hashtags to be appended with the url
    @param text Unformatted status
   */
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

  /*
    mentions should link to the user profile at http://twitter.com/User
    @param mentions list of mentions to be appended with the url
    @param text Unformatted status
   */
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

  /*
  All the other entities that have URLs should link to their resolved expanded URL, with the
  display URL as the anchor text.
  @param urls list of urls to be appended with the expanded url
  @param text Unformatted status
   */
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
