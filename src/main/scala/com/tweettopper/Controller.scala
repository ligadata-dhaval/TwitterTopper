package com.tweettopper

import org.springframework.web.bind.annotation._

/**
 * Controller for the web service which performs the routing of the requests
 */
@RestController
class Controller {
  /*
    @param screen_names  A comma separated list of screen names of users whose statuses we're fetching
    @param cursor When the response object has a non-null 'next_cursor' member, the value may be passed as the 'cursor' parameter in the next request to fetch the next page of results.
    @param count No of statuses to be retrieved
   */
  @RequestBody
  @RequestMapping(value = Array("/statuses"))
  def viewStatus(@RequestParam("screen_names") screenNames: String, @RequestParam("count") count: Int, @RequestParam(required = false) cursor: String): String = {
    new Service().getStatuses(screenNames, count + 1, cursor)
  }
}

