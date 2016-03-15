package co.vine.ws.co.vine.ws.vo

import scala.beans.BeanProperty

/**
 * Created by dhavalkolapkar on 3/15/16.
 */

case class ResponseBody(@BeanProperty nextCursor: String, @BeanProperty statuses: Array[String]) {}

