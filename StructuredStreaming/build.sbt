ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "TrackABoat",
    scalaVersion := "2.13.15",
    assembly / mainClass := Some("esgi.datastreaming.org.StructuredStreaming"),

    // The crucial part: merge strategy
    assembly / assemblyMergeStrategy := {
      val defaultStrategy = (assembly / assemblyMergeStrategy).value

      {
        case PathList("META-INF", "services", xs @ _*) =>
          MergeStrategy.concat

        case "META-INF/io.netty.versions.properties" =>
          MergeStrategy.first

        case "module-info.class" =>
          MergeStrategy.discard

        case PathList("META-INF", "versions", _ @ _*) =>
          MergeStrategy.first

        case PathList("google", "protobuf", _ @ _*) =>
          MergeStrategy.first

        case PathList("META-INF", "org", "apache", "logging", "log4j", "core", "config", "plugins", "Log4j2Plugins.dat") =>
          MergeStrategy.first

        case PathList("org", "apache", "commons", "logging", _ @ _*) =>
          MergeStrategy.first

        case PathList("arrow-git.properties") =>
          MergeStrategy.first

        // Default/fallback: use the 'old' (a.k.a. default) strategy
        case x => defaultStrategy(x)
      }
    }
  )

val sparkVersion = "3.5.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.kafka" % "kafka-clients" % "3.4.1",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-token-provider-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-tags" % sparkVersion,
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.3",
  "org.postgresql" % "postgresql" % "42.6.0",
  "io.github.cdimascio" % "java-dotenv" % "5.2.2",
  "com.typesafe" % "config" % "1.4.2",
  "org.apache.spark" %% "spark-avro" % "3.5.4",
  "org.apache.avro" % "avro" % "1.11.4",
  "io.confluent" % "kafka-avro-serializer" % "7.4.8"
)

// Add the Confluent repository
resolvers ++= Seq(
  "Confluent Maven Repository" at "https://packages.confluent.io/maven/"
)
