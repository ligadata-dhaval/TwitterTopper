package co.vine.ws

import java.net.URLEncoder

import twitter4j.{Twitter, TwitterFactory}
import twitter4j.auth.OAuth2Token
import twitter4j.conf.ConfigurationBuilder

import scalaj.http._
/**
 * Created by dhavalkolapkar on 3/9/16.
 */
class Service {

  /* def encodeKeys(consumerKey: String, consumerSecret: String): String ={
    val encodedConsumerKey=URLEncoder.encode(consumerKey,"UTF-8")
    val encodedConsumerSecret=URLEncoder.encode(consumerSecret,"UTF-8")
    val bearerTokenCredentials=encodedConsumerKey +":"+encodedConsumerSecret
    val encodedBearerTokenCredentials=Base64.encode(bearerTokenCredentials.getBytes()).toString
    encodedBearerTokenCredentials
  }

  def requestBearerToken: Unit ={

  }*/

  def getOAuth2Token: OAuth2Token = {
    var token: OAuth2Token=null
    var cb: ConfigurationBuilder=null
    cb=new ConfigurationBuilder
    cb.setApplicationOnlyAuthEnabled(true)
    cb.setOAuthConsumerKey("EvDxkfP3xzlZp9Ss5HoTxBFu4")
    cb.setOAuthConsumerSecret("HoBfu1L2MLlozYhY2Gq99FtF3ZQIWyKVl2hjXEbJIUCAHL4sZ8")
    new TwitterFactory(cb.build()).getInstance().getOAuth2Token
  }

    def viewTweets(screen_names: String): Unit ={
      var screenNamesArr=screen_names.split(",")
      var token: OAuth2Token=getOAuth2Token
     var cb: ConfigurationBuilder  = new ConfigurationBuilder()
      cb.setApplicationOnlyAuthEnabled(true)
      cb.setOAuthConsumerKey("EvDxkfP3xzlZp9Ss5HoTxBFu4")
      cb.setOAuthConsumerSecret("HoBfu1L2MLlozYhY2Gq99FtF3ZQIWyKVl2hjXEbJIUCAHL4sZ8")
      cb.setOAuth2TokenType(token.getTokenType())
      cb.setOAuth2AccessToken(token.getAccessToken())
     var twitter: Twitter  = new TwitterFactory(cb.build()).getInstance()
      val user = twitter.showUser(screenNamesArr(0))
      println(user.getStatus)
    }
}
