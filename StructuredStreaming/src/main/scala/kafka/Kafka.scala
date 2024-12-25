package esgi.datastreming.org
package kafka

import config.ConfigLoader

import org.apache.spark.sql.functions.{col, struct, to_json, lit}
import org.apache.spark.sql.{DataFrame, SparkSession}

object Kafka {
  def loadKafkaStream(spark: SparkSession): DataFrame = {
    println("Loading kafka data")

    // Fetch DataFrame from Kafka topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.LoadKafkaConfig.bootstrapServers)
      .option("subscribe", ConfigLoader.LoadKafkaConfig.topic)
      .load()

    df
  }

  def writeKafkaStream(df: DataFrame): Unit = {
    // Set the key for the Kafka topic
    val keyColumn = if (df.columns.contains("ImoNumber")) {
      lit("shipInfo")
    } else if (df.columns.contains("ship_id")) {
      lit("shipLocation")
    } else {
      lit(null)
    }

    // Transform DataFrame for Kafka key-value pairs
    val kafkaDf = df
      .withColumn("key", keyColumn.cast("string")) // Cast ImoNumber to string for the key
      .withColumn("value", to_json(struct(df.columns.map(col): _*))) // Convert all columns to JSON for the value

    kafkaDf.write
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.WriteKafkaConfig.bootstrapServers)
      .option("topic", ConfigLoader.WriteKafkaConfig.topic)
      .save()
  }
}
