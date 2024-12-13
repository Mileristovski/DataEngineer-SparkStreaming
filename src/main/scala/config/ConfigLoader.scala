package esgi.datastreming.org
package config

import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.Dotenv

object ConfigLoader {
  private val config = ConfigFactory.load()
  private val dotenv = Dotenv.load()

  object DbConfig {
    private val dbConfig = config.getConfig("db")
    val name: String = dbConfig.getString("name")
    val user: String = dbConfig.getString("user")
    val jdbc: String = dbConfig.getString("jdbc")
    val driver: String = dbConfig.getString("driver")

    val dbPassword: String = dotenv.get("DB_PASSWORD")
  }

  object KafkaConfig {
    private val kafkaConfig = config.getConfig("kafka")
    val bootstrapServers: String = kafkaConfig.getString("bootstrapServers")
    val topic: String = kafkaConfig.getString("topic")
  }
}
