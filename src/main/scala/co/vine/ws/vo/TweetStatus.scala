package co.vine.ws.vo

/**
 * Class represents the statuses and username
 *
 * @constructor create a new person with a name and age.
 * @param user The screen name of the status author
 * @param status An HTML version of the status text, ready to be displayed by the web app using this API
 */

case class TweetStatus(user: String, status: String) {}

