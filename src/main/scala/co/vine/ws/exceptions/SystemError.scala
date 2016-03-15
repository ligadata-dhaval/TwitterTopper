package co.vine.ws.exceptions

/**
 * Created by dhavalkolapkar on 3/15/16.
 */
class SystemError(message: String, nestedException: Throwable) extends Exception(message, nestedException) {
  def this() = this("", null)
  def this(message: String) = this(message, null)
  def this(nestedException : Throwable) = this("", nestedException)
}
