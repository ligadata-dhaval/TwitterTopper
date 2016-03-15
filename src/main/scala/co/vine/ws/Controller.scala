package co.vine.ws

import org.springframework.web.bind.annotation._

/**
 * Created by dhavalkolapkar on 3/9/16.
 */

@RestController
class Controller {

  @RequestMapping(value = Array("/statuses"))
  def viewStatus(@RequestParam("screen_names") screenNames: String, @RequestParam("count") count: Int, @RequestParam(required = false) cursor: String): Unit ={
      println("Cursor is: "+cursor)
     new Service().getStatuses(screenNames,count+1,cursor)
     // new Twitter4JService().getStatuses
  }
}
