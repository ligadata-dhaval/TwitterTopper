package co.vine.ws

import co.vine.ws.exceptions.SystemError
import co.vine.ws.utility.{TwitterDsl, TwitterUtility}
import co.vine.ws.vo.{ResponseBody, TweetStatus}
import com.github.scribejava.core.oauth._
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.util.control.Exception.catching
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.exceptions.OAuthException
import com.github.scribejava.core.model._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by dhavalkolapkar on 3/9/16.
 */


class Service {

  def getStatuses(screenNames: String, count: Int, cursor: String): String = {
    (TwitterDsl.getUserListStatuses(TwitterDsl.addMembersToList(screenNames, TwitterDsl.createUsersList()), cursor, count))
  }

}