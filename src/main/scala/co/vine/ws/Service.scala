package co.vine.ws

import co.vine.ws.utility.TwitterDsl

/*
 * Retrieve the most recent statuses among the requested users.
* @param screen_names  A comma separated list of screen names of users whose statuses we're fetching
* @param count  Number of results this request should return.
* @param cursor optional. When the response object has a non-null 'next_cursor' member, the value may be passed as the 'cursor' parameter in the next request to fetch the next page of results.
 */
class Service{
/*
    Retrieve the most recent statuses among the requested users.
    @param
 */
  def getStatuses(screenNames: String, count: Int, cursor: String): String = {
    (TwitterDsl.getUserListStatuses(TwitterDsl.addMembersToList(screenNames, TwitterDsl.createUsersList()), cursor, count))
  }

}