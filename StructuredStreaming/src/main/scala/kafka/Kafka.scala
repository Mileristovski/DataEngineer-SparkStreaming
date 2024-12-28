package esgi.datastreming.org
package kafka

import config.ConfigLoader

import org.apache.spark.sql.{DataFrame, SparkSession, functions => F}

object Kafka {
  def loadKafkaStream(spark: SparkSession): DataFrame = {
    // Fetch DataFrame from Kafka topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.Kafka.bootstrapServers)
      .option("subscribe", ConfigLoader.Kafka.input)
      .load()

    df
  }

  def writeKafkaTopic(df: DataFrame, topicName: String): Unit = {
    // Transform DataFrame for Kafka key-value pairs
    df.write
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.Kafka.bootstrapServers)
      .option("topic", topicName)
      .save()
  }
}
