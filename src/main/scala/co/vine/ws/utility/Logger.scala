package co.vine.ws.utility

/**
 * Logger service.
 * Log4j2 for logging.
 */
trait Logger {
  def logger = Logger.logger
}

object Logger {
  val logger: org.apache.logging.log4j.Logger = org.apache.logging.log4j.LogManager.getLogger(this.getClass)
}
