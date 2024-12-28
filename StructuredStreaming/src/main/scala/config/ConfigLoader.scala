package esgi.datastreming.org
package config

import com.typesafe.config.ConfigFactory

object ConfigLoader {
  private val config = ConfigFactory.load()

  object Kafka {
    private val loadKafkaConfig = config.getConfig("kafka")
    val bootstrapServers: String = loadKafkaConfig.getString("bootstrapServers")
    val input: String = loadKafkaConfig.getString("topicInput")
    val ships: String = loadKafkaConfig.getString("topicShips")
    val positions: String = loadKafkaConfig.getString("topicPositions")
  }
}
