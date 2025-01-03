package esgi.datastreaming.org

import esgi.datastreaming.org.handlers.{MessageHandler, MetaDataHandler, ShipStaticDataHandler}
import esgi.datastreaming.org.kafka.Kafka.loadKafkaStream
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object StructuredStreaming {
  def main(args: Array[String]): Unit = {

    // Initialize a spark session
    val spark = SparkSession
      .builder
      .appName("StructuredStreaming")
      .master("local[*]")
      .getOrCreate()

    // Loading the kafka stream
    val df: DataFrame = loadKafkaStream(spark)

    val jsonDf: DataFrame = df.withColumn("Message", col("value").cast("string"))

    // Define handlers
    val handlers: Seq[MessageHandler] = Seq(ShipStaticDataHandler, MetaDataHandler)

    // Start all queries
    handlers.map(handler => handler.handle(jsonDf))

    // Await termination for all streams
    spark.streams.awaitAnyTermination()
  }
}
