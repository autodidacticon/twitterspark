name := "twitterspark"
organization := "io.moorhead"
version := "0.0.1"

scalaVersion := "2.11.8"

lazy val sparkVersion = "2.4.0"

lazy val scalatraVersion = "2.6.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.twitter4j" % "twitter4j-stream" % "4.0.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-log4j12" % "1.7.10",
  "org.scalatra" %% "scalatra" % scalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "redis.clients" % "jedis" % "3.0.1",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
) ++ testDependencies

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)