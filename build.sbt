import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.rjs.Import._

import WebJs._
import RjsKeys._


name := """activator-reactive-fx"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)


scalaVersion := "2.11.7"

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:higherKinds",
  "-unchecked",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  //"-Ywarn-unused",
  //"-Ywarn-unused-import",
  "-Ywarn-value-discard"
)


val scalaLoggingVersion = "3.1.0"
val logbackVersion = "1.1.2"
val akkaVersion = "2.3.13"
val akkaHTTPVersion = "2.0-M2"

val webjarsJqueryVersion = "2.1.4"
val webjarsBootswatchVersion = "3.3.5+4"
val webjarsBootstrapVersion = "3.3.6"
val webjarsReqjsVersion = "2.1.22"
val webjarsReqjsTxtVersion = "2.0.14-1"
val webjarsReactJsVersion = "0.14.3"
val webjarsJsSignalsVersion = "1.0.0"
val webjarsLoDashVersion = "3.10.1"



val loggingScala    = "com.typesafe.scala-logging"  %% "scala-logging"                  % scalaLoggingVersion
val loggingLogback  = "ch.qos.logback"              %  "logback-classic"                % logbackVersion
val akkaSlf4j       = "com.typesafe.akka"           %% "akka-slf4j"                     % akkaVersion
val akkaHttpCore    = "com.typesafe.akka"           %% "akka-http-core-experimental"    % akkaHTTPVersion
val akkaHttp        = "com.typesafe.akka"           %% "akka-http-experimental"         % akkaHTTPVersion


val webjarsJquery   = "org.webjars"                 %  "jquery"                         % webjarsJqueryVersion
val webjarsBootswatch = "org.webjars"               %  "bootswatch-cosmo"               % webjarsBootswatchVersion
val webjarsBootstrap= "org.webjars"                 %  "bootstrap"                      % webjarsBootstrapVersion
val webjarsReqjs    = "org.webjars"                 %  "requirejs"                      % webjarsReqjsVersion
val webjarsReqjsTxt = "org.webjars"                 %  "requirejs-text"                 % webjarsReqjsTxtVersion
val webjarsReactJs  = "org.webjars"                 %  "react"                          % webjarsReactJsVersion
val webjarsJsSignals= "org.webjars"                 %  "js-signals"                     % webjarsJsSignalsVersion
val webjarsLoDash   = "org.webjars"                 %  "lodash"                         % webjarsLoDashVersion



libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "2.0-M2",
  loggingLogback,
  loggingScala,
  akkaSlf4j,
  akkaHttpCore,
  akkaHttp,
  webjarsBootstrap,
  webjarsJquery,
  webjarsReactJs,
  webjarsReqjs,
  webjarsReqjsTxt,
  webjarsJsSignals,
  webjarsLoDash
)

includeFilter in(Assets, LessKeys.less) := "*.less"
excludeFilter in(Assets, LessKeys.less) := "_*.less"


pipelineStages := Seq(rjs, digest, gzip)



//fork in run := true