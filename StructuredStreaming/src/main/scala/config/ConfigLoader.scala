package esgi.datastreming.org
package config

import io.github.cdimascio.dotenv.Dotenv

object ConfigLoader {
  private val dotenv = Dotenv.load()

  object Kafka {
    val bootstrapServers: String = dotenv.get("KAFKA_BOOTSTRAP_SERVERS")
    val input: String = dotenv.get("KAFKA_TOPIC_INPUT")
    val ships: String = dotenv.get("KAFKA_TOPIC_SHIPS")
    val positions: String = dotenv.get("KAFKA_TOPIC_POSITIONS")
  }
}
