name := "diabetes-scala"
version := "1.0"
scalaVersion := "2.12.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

herokuAppName in Compile := "whyamidying"


libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.5",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0"
)

libraryDependencies += guice

