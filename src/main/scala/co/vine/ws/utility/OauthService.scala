package co.vine.ws.utility

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.oauth.OAuth10aService

/**
 * Created by dhavalkolapkar on 3/16/16.
 */

object OauthService{
lazy val  service= new ServiceBuilder()
    .apiKey("jK6k4cbCfO191K2tezqm1eZqQ")
    .apiSecret("YkqNYCjkFUHqQLn7Kh5IxpgFJ8hqIrQcZsWPil4PWh6moYbQhg")
    .build(TwitterApi.Authenticate.instance())

 lazy val accessToken = new OAuth1AccessToken("220347935-yNr3XU7UV5JcRWxKRZVJLgHWesjVIJdtYap5n7KL", "11JtXVWzJd4GnCk84dtxEA06x4QkTInNy6W25OWSNhRTZ")
}
