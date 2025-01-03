package esgi.datastreaming.org
package config

object ConfigLoader {
  object Kafka {
    val bootstrapServers: String =
      sys.env.getOrElse("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    val schemaRegistryUrl: String =
      sys.env.getOrElse("KAFKA_SCHEMA_URL", "http://localhost:8081")
    val input: String =
      sys.env.getOrElse("KAFKA_TOPIC_INPUT", "ais_data")
    val ships: String =
      sys.env.getOrElse("KAFKA_TOPIC_SHIPS", "ais_data_ships")
    val positions: String =
      sys.env.getOrElse("KAFKA_TOPIC_POSITIONS", "ais_data_positions")
  }
}
