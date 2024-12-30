package esgi.datastreming.org
package kafka

import config.ConfigLoader

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.sql.{DataFrame, SparkSession, functions => F}

import java.util.Properties

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

  private def configureKafkaProducer(): KafkaProducer[String, GenericRecord] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])
    props.put("schema.registry.url", "http://localhost:8081")

    val producer = new KafkaProducer[String, GenericRecord](props)
    producer
  }

  def writeKafkaTopic(avroRecord: GenericRecord, topicName: String, key: String): Unit = {
      val producer: KafkaProducer[String, GenericRecord] = configureKafkaProducer()
      val record = new ProducerRecord[String, GenericRecord](topicName, key, avroRecord)

      try {
        producer.send(record)
      } catch {
        case e: Exception =>
          // Handle SerializationException or other exceptions if necessary
          println(s"Error sending record: ${e.getMessage}")
      } finally {
        // Ensure producer resources are released
        producer.flush()
        producer.close()
      }
  }
}
