package co.vine.ws

import org.springframework.web.bind.annotation._

/**
 * Created by dhavalkolapkar on 3/9/16.
 */

@RestController
class Controller {

  @RequestMapping(value = Array("/statuses"))
  @ResponseBody
  def viewStatus(@RequestParam("screen_names") screenNames: String, @RequestParam("count") count: Int): Unit ={
    println("Screen names are: "+screenNames+" and count is: "+count)
   var service= new Service
    service.viewTweets(screenNames)
  }
}
