package esgi.datastreming.org
package kafka

import config.ConfigLoader

import org.apache.spark.sql.functions.{col, struct, to_json, lit}
import org.apache.spark.sql.{DataFrame, SparkSession, functions => F}

object Kafka {
  def loadKafkaStream(spark: SparkSession): DataFrame = {
    println("Loading kafka data")

    // Fetch DataFrame from Kafka topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.Kafka.bootstrapServers)
      .option("subscribe", ConfigLoader.Kafka.input)
      .load()

    df
  }

  def writeKafkaTopicShips(df: DataFrame): Unit = {
    // Transform DataFrame for Kafka key-value pairs
    df.write
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.Kafka.bootstrapServers)
      .option("topic", ConfigLoader.Kafka.ships)
      .save()
  }

  def writeKafkaTopicPositions(df: DataFrame): Unit = {
    // Transform DataFrame for Kafka key-value pairs
    val kafkaDf = df
      .withColumn("key", lit("ais_positions").cast("string"))
      .withColumn("value", to_json(struct(df.columns.map(col): _*)))

    kafkaDf.write
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.Kafka.bootstrapServers)
      .option("topic", ConfigLoader.Kafka.positions)
      .save()
  }
}
