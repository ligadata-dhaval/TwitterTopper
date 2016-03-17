package co.vine.ws.utlity

/**
 * Created by dhavalkolapkar on 3/16/16.
 */
trait Logger {
  def logger = Logger.logger
}

object Logger {
  val logger: org.apache.logging.log4j.Logger = org.apache.logging.log4j.LogManager.getLogger(this.getClass)
}
