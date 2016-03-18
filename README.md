# TweetTopper
A simple web application that  provides a single API endpoint and which may be used by a simple website to display and page through a time-ordered list of statuses of several Twitter users. The endpoint is accessible by anonymous users, as the service handles the necessary Twitter API authentication on their behalf.

Programming Language: Scala
Reason:Allows you to construct elegant class hierarchies for maximum code reuse and extensibility, and implement their behavior using higher-order functions.

Twitter Authentication: The application uses scribe java for OAUTH 2.0.
Reason: A single line is the only thing you need to configure ScribeJava with LinkedIn's OAuth API. It is also modular, thread safe and has less memory footprint.

Application framework: Bootstrapping and development of the Spring application using Spring-boot micro-framework.
Reason: Creates Spring-powered, production-grade applications and services by providing dependency management, auto-configuration and advanced externalized configuration.

Unit test: ScalaTest
Description: Used flatspec variant for unit tests and performed 90% code coverage.
Location: src->test -> scala -> com -> tweettopper -> MasterTestSuite.

Documentation: Scaladoc
Location: TweetTopper -> Documentation

Logger: Log4j2
Location of configuration: src -> main -> resources -> log4j2.xml
Location of logs which are generated: TweetTopper -> logs

Code flow and approach:
Controller.scala performs the parsing of the request params and routing it. The service class performs the important task of calling the appropriate methods and helpers to obtain the desired result.
The ResponseBody and the TweetStatus form the value objects which store the status details retrieved using the Twitter's REST API. The actual calls to the twitter's API are performed in the TwitterDsl class.
Here, instead of retrieving each users x no of statuses, combining them and then time ordering them to get the top x statuses, the application creates a users list and adds members to it based on the given screen_names. This helps the application to make fewer calls to the twitter to retrieve time ordered statuses and also which are limited by the count variable.

Consider an example where user requests 10 screen_names and gives the count as 30. By usual approach we would need to retrieve 30 statuses of each user which makes 300 (30x10) status retrievals. These statuses are then combined and then sorted based on the creation time. Out of these sorted 300 statuses we choose only 30 statuses. This is wastage of space, time and computation.

Instead of this old approach, I have created a list wherein I add the 10 screen_names. While making the twitter REST call itself I ask for only 30 statuses. This returns the lastest 40 statuses of the screen_names. It also provides me a cursor. If I add this cursor value in the next request, it provides me the next 30 statuses of those users.

NOTE: As per twitter, 