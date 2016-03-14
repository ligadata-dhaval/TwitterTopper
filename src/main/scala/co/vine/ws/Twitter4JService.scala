package co.vine.ws

import twitter4j.{Paging, UserList, TwitterFactory}
import twitter4j.auth.AccessToken

/**
 * Created by dhavalkolapkar on 3/14/16.
 */
class Twitter4JService {

  def getStatuses: Unit = {
    val twitter = TwitterFactory.getSingleton()
    twitter.setOAuthConsumer("jK6k4cbCfO191K2tezqm1eZqQ", "YkqNYCjkFUHqQLn7Kh5IxpgFJ8hqIrQcZsWPil4PWh6moYbQhg")
    var accessToken = new AccessToken("220347935-yNr3XU7UV5JcRWxKRZVJLgHWesjVIJdtYap5n7KL", "11JtXVWzJd4GnCk84dtxEA06x4QkTInNy6W25OWSNhRTZ")
    twitter.setOAuthAccessToken(accessToken)
    val myList: UserList = twitter.createUserList("Bawa", true, "bawa re bawa!")
    var slug = myList.getSlug
    println("My list slug is: " + slug)
    var ownerId=twitter.users().showUser("PunyacheRau").getId
    var id1=twitter.users().showUser("NargisFakhri").getId
    println("Hot Nargis Id: "+id1)
    twitter.createUserListMember(ownerId,slug,id1)
    var paging=new Paging(1,10)
    println("No of members: "+myList.getMemberCount)
   var userStatuses= twitter.list.getUserListStatuses(ownerId,slug,paging)
    userStatuses.toArray().foreach(println)
  }
}
