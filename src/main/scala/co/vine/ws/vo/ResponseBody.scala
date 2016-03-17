package co.vine.ws.vo

import scala.beans.BeanProperty

/**
 * The class represents the most recent statuses among the requested users and the next cursor
 * @param nextCursor Null or a string value that may be passed as 'cursor' in a follow up request to fetch the next-most-recent
page of statuses.
 * @param statuses An array of status objects. These statuses should be the tweets from the requested users, most recent first
 */

case class ResponseBody(@BeanProperty nextCursor: String, @BeanProperty statuses: Array[String]) {}

