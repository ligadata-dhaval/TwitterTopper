@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  VineWS startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and VINE_WS_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\vine-ws-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\spring-boot-starter-web-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-actuator-1.3.3.RELEASE.jar;%APP_HOME%\lib\scala-library-2.11.7.jar;%APP_HOME%\lib\scribejava-apis-2.3.0.jar;%APP_HOME%\lib\play_2.11-2.5.0.jar;%APP_HOME%\lib\log4j-api-2.1.jar;%APP_HOME%\lib\log4j-core-2.1.jar;%APP_HOME%\lib\scalatest_2.11-3.0.0-M15.jar;%APP_HOME%\lib\spring-boot-starter-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-validation-1.3.3.RELEASE.jar;%APP_HOME%\lib\jackson-databind-2.6.5.jar;%APP_HOME%\lib\spring-web-4.2.5.RELEASE.jar;%APP_HOME%\lib\spring-webmvc-4.2.5.RELEASE.jar;%APP_HOME%\lib\spring-boot-actuator-1.3.3.RELEASE.jar;%APP_HOME%\lib\scribejava-core-2.3.0.jar;%APP_HOME%\lib\build-link-2.5.0.jar;%APP_HOME%\lib\play-iteratees_2.11-2.5.0.jar;%APP_HOME%\lib\play-json_2.11-2.5.0.jar;%APP_HOME%\lib\play-netty-utils-2.5.0.jar;%APP_HOME%\lib\play-streams_2.11-2.5.0.jar;%APP_HOME%\lib\twirl-api_2.11-1.1.1.jar;%APP_HOME%\lib\slf4j-api-1.7.16.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.16.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.16.jar;%APP_HOME%\lib\akka-actor_2.11-2.4.2.jar;%APP_HOME%\lib\akka-slf4j_2.11-2.4.2.jar;%APP_HOME%\lib\jackson-core-2.6.5.jar;%APP_HOME%\lib\jackson-annotations-2.6.5.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.6.5.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.6.5.jar;%APP_HOME%\lib\scala-stm_2.11-0.7.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\joda-time-2.8.2.jar;%APP_HOME%\lib\joda-convert-1.8.1.jar;%APP_HOME%\lib\commons-lang3-3.4.jar;%APP_HOME%\lib\xercesImpl-2.11.0.jar;%APP_HOME%\lib\jta-1.1.jar;%APP_HOME%\lib\guice-4.0.jar;%APP_HOME%\lib\guice-assistedinject-4.0.jar;%APP_HOME%\lib\scala-reflect-2.11.7.jar;%APP_HOME%\lib\scala-java8-compat_2.11-0.7.0.jar;%APP_HOME%\lib\scala-parser-combinators_2.11-1.0.4.jar;%APP_HOME%\lib\scalactic_2.11-3.0.0-M15.jar;%APP_HOME%\lib\scala-xml_2.11-1.0.2.jar;%APP_HOME%\lib\spring-boot-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-boot-autoconfigure-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-logging-1.3.3.RELEASE.jar;%APP_HOME%\lib\spring-core-4.2.5.RELEASE.jar;%APP_HOME%\lib\snakeyaml-1.16.jar;%APP_HOME%\lib\tomcat-embed-core-8.0.32.jar;%APP_HOME%\lib\tomcat-embed-el-8.0.32.jar;%APP_HOME%\lib\tomcat-embed-logging-juli-8.0.32.jar;%APP_HOME%\lib\tomcat-embed-websocket-8.0.32.jar;%APP_HOME%\lib\hibernate-validator-5.2.4.Final.jar;%APP_HOME%\lib\spring-aop-4.2.5.RELEASE.jar;%APP_HOME%\lib\spring-beans-4.2.5.RELEASE.jar;%APP_HOME%\lib\spring-context-4.2.5.RELEASE.jar;%APP_HOME%\lib\spring-expression-4.2.5.RELEASE.jar;%APP_HOME%\lib\play-exceptions-2.5.0.jar;%APP_HOME%\lib\config-1.3.0.jar;%APP_HOME%\lib\play-functional_2.11-2.5.0.jar;%APP_HOME%\lib\play-datacommons_2.11-2.5.0.jar;%APP_HOME%\lib\reactive-streams-1.0.0.jar;%APP_HOME%\lib\akka-stream_2.11-2.4.2.jar;%APP_HOME%\lib\xml-apis-1.4.01.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\guava-16.0.1.jar;%APP_HOME%\lib\logback-classic-1.1.5.jar;%APP_HOME%\lib\log4j-over-slf4j-1.7.16.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\jboss-logging-3.3.0.Final.jar;%APP_HOME%\lib\classmate-1.1.0.jar;%APP_HOME%\lib\ssl-config-akka_2.11-0.1.3.jar;%APP_HOME%\lib\logback-core-1.1.5.jar;%APP_HOME%\lib\ssl-config-core_2.11-0.1.3.jar

@rem Execute VineWS
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %VINE_WS_OPTS%  -classpath "%CLASSPATH%" co.vine.ws.TwitterApplication %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable VINE_WS_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%VINE_WS_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
