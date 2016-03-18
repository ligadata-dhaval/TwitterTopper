# TweetTopper
A simple web application that  provides a single API endpoint and which may be used by a simple website to display and page through a time-ordered list of statuses of several Twitter users. The endpoint is accessible by anonymous users, as the service handles the necessary Twitter API authentication on their behalf.

Twitter Authentication: The application uses scribe java for OAUTH 2.0.
Reason: A single line is the only thing you need to configure ScribeJava with LinkedIn's OAuth API. It is also modular, thread safe and has less memory footprint.

Application framework: Bootstrapping and development of the Spring application using Spring-boot micro-framework.
Reason: Creates Spring-powered, production-grade applications and services by providing dependency management, auto-configuration and advanced externalized configuration.

