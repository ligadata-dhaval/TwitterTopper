package co.vine.ws

import org.springframework.web.bind.annotation._
import play.api.libs.json.{Json, JsValue}

/**
 * Created by dhavalkolapkar on 3/9/16.
 */

@RestController
class Controller {
  @RequestBody
  @RequestMapping(value = Array("/statuses"))
  def viewStatus(@RequestParam("screen_names") screenNames: String, @RequestParam("count") count: Int, @RequestParam(required = false) cursor: String): String = {
    new Service().getStatuses(screenNames, count + 1, cursor)
  }
}

