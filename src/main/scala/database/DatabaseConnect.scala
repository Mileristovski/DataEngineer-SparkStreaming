package esgi.datastreming.org
package database

import config.ConfigLoader

object DatabaseConnect {
  def connect(): java.util.Properties = {
    println("Connecting to db...")

    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", ConfigLoader.DbConfig.user)
    connectionProperties.setProperty("password", ConfigLoader.DbConfig.dbPassword)
    connectionProperties.setProperty("driver", ConfigLoader.DbConfig.driver)

    connectionProperties
  }
}
