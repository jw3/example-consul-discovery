
lazy val `example-consul-discovery` =
  project.in(file("."))
  .aggregate(api, service)

lazy val api =
  project.in(file("api"))
  .settings(commonSettings: _*)

lazy val service =
  project.in(file("service"))
  .settings(commonSettings: _*)
  .settings(dockerSettings: _*)
  .enablePlugins(JavaServerAppPackaging, OpenShiftPlugin)

lazy val commonSettings = Seq(
  organization := "com.github.jw3",
  scalaVersion := "2.12.3",
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",

    "-feature",
    "-unchecked",
    "-deprecation",

    "-language:postfixOps",
    "-language:implicitConversions",

    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-Xlint:_"
  ),
  libraryDependencies ++= commonLibraries
)

lazy val akkaVersion = "2.5.2"
lazy val scalatestVersion = "3.0.3"

lazy val commonLibraries = {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,

    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

    "org.scalactic" %% "scalactic" % scalatestVersion % Test,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )
}

lazy val dockerSettings = Seq(
  packageName in Docker := "example-consul-service-discovery",
  dockerUpdateLatest := true
)
