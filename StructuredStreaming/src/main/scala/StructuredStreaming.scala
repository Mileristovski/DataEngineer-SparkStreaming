package esgi.datastreming.org

import database.DatabaseConnect
import handlers.{MessageHandler, MetaDataHandler, ShipStaticDataHandler}
import kafka.Kafka.loadKafkaStream

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

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
    val jsonDf: DataFrame = df.withColumn("Message", col("value").cast("string"))

    val connectionProperties: Properties = DatabaseConnect.connect()

    // Define handlers
    val handlers: Seq[MessageHandler] = Seq(ShipStaticDataHandler, MetaDataHandler)

    // Start all queries
    handlers.map(handler => handler.handle(jsonDf, connectionProperties))

    // Await termination for all streams
    spark.streams.awaitAnyTermination()
  }
}
