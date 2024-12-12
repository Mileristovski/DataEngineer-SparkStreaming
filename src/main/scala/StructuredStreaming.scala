package esgi.datastreming.org

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import database.DatabaseConnect
import handlers.{MessageHandler, MetaDataHandler, PositionReportHandler}
import kafka.Kafka.loadKafkaStream

import java.util.Properties

object StructuredStreaming {
  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties")

    // Initialize a spark session
    val spark = SparkSession
      .builder
      .appName("StructuredStreaming")
      .master("local[*]")
      .getOrCreate()

    // Loading the kafka stream
    val df: DataFrame = loadKafkaStream(spark)

    // Get the complete message from the Kafka topic
    val jsonDf: DataFrame = df.withColumn("Message", col("value").cast("string"))
    val connectionProperties: Properties = DatabaseConnect.connect()

    // Define handlers
    val handlers: Seq[MessageHandler] = Seq(MetaDataHandler)

    // Start all queries
    handlers.map(handler => handler.handle(jsonDf, connectionProperties))

    // Await termination for all streams
    spark.streams.awaitAnyTermination()
  }
}
