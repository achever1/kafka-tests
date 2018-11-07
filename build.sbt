

name := "kafka-tests"

version := "0.1"

scalaVersion := "2.12.7"

//fork in run := true

lazy val `kafka-tests-root` = (project in file("."))
  .aggregate(`kafka-tests-svc`)

lazy val `kafka-tests-svc` = project
  .enablePlugins(PlayScala)
  .dependsOn(`kafka-tests-module`)
  .settings(
    name := "kafka-tests-svc",
    libraryDependencies +=  guice,
    PlayKeys.playDefaultPort := 8000
  )

lazy val `kafka-tests-module` = project
  .settings(
    name := "kafka-tests-module",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-kafka" % "0.20",
      "ch.qos.logback" % "logback-classic" % "1.2.3")
  )
