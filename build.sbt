name := "AkkaHttpLaunchPad"

version := "0.1"

scalaVersion := "2.12.6"

resolvers += "AWS DynamoDB" at "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"

libraryDependencies ++= {
  val akkaHttpVersion = "10.1.3"
  val akkaVersion = "2.5.13"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.scalaz" % "scalaz-scalacheck-binding_2.12" % "7.3.0-M15",
    "org.scalatest" % "scalatest_2.12" % "3.0.3" % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.google.inject" % "guice" % "4.1.0",
    "org.typelevel" %% "cats-core" % "1.0.0-MF",
    "com.amazonaws" % "aws-java-sdk" % "1.11.192",
    "com.gu" %% "scanamo" % "0.9.5",
    "com.typesafe.play" %% "play-json" % "2.6.8",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "net.debasishg" %% "redisclient" % "3.4",
    "com.typesafe.play" %% "play-ws" % "2.6.17",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.9",
    "net.debasishg" %% "redisclient" % "3.4"
  )
}