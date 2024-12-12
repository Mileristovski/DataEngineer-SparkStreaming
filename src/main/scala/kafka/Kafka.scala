package esgi.datastreming.org
package kafka

import config.ConfigLoader
import org.apache.spark.sql.{DataFrame, SparkSession}

object Kafka {
  def loadKafkaStream(spark: SparkSession): DataFrame = {
    println("Loading kafka data")

    // Fetch DataFrame from Kafka topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", ConfigLoader.KafkaConfig.bootstrapServers)
      .option("subscribe", ConfigLoader.KafkaConfig.topic)
      .load()

    df
  }
}
