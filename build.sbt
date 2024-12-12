ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "TrackABoat",
    idePackagePrefix := Some("esgi.datastreming.org")
  )

val sparkVersion = "3.5.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.kafka" % "kafka-clients" % "3.4.1",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.3",
  "org.apache.spark" %% "spark-token-provider-kafka-0-10" % "3.5.3",
  "org.apache.spark" %% "spark-tags" % "3.5.3",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.3",
  "org.postgresql" % "postgresql" % "42.6.0",
  "io.github.cdimascio" % "java-dotenv" % "5.2.2",
  "com.typesafe" % "config" % "1.4.2"
)
