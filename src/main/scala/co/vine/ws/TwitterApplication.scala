package co.vine.ws

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * The application retrieves the most recent statuses among the requested users. This is limited by count given by the user.
 * E.g. Users requests latest 10 statues from Dhaval, Dan, Willi. The program will return the most recent 10 statues among these users.
 * User can also request for the next 10 statuses by giving the next_cursor.
 *
 * @author  Dhaval Kolapkar
 * @version 1.0
 * @since   10 March 2016
 */
@SpringBootApplication
class TwitterApplication {}

object TwitterApplication extends App {
  SpringApplication run classOf[TwitterApplication]
}