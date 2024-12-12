package esgi.datastreming.org
package handlers

import config.ConfigLoader
import database.Schemas.metaDataSchema

import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

import java.util.Properties

object MetaDataHandler extends MessageHandler {
  override def messageType: String = ""

  override def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery = {
    // Parse MetaData
    val withParsedMeta = jsonDf
      .withColumn("MetaData", from_json(col("Message"), metaDataSchema))

    // Parsing the MetaData from the initial DataFrame
    val parsedDf = withParsedMeta.select(
        col("MetaData.MetaData.MMSI").as("MMSI"),
        col("MetaData.MetaData.ShipName").as("ShipName"),
        col("MetaData.MetaData.latitude").as("latitude"),
        col("MetaData.MetaData.longitude").as("longitude")
      )

    // Writing data to the Database
    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        val spark = batchDf.sparkSession

        // Read existing ships from DB
        val shipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)

        // Join batch data with ships on MMSI and ShipName to find matching ships
        val batchWithShipId = batchDf.join(shipsDf, Seq("MMSI", "ShipName"), "inner")
          .select(
            col("id").as("ship_id"),
            col("latitude"),
            col("longitude")
          )

        // Insert positions into ais_positions only if matching ships are found
        if (!batchWithShipId.isEmpty) {
          batchWithShipId.write
            .mode("append")
            .jdbc(ConfigLoader.DbConfig.jdbc, "ais_positions", connectionProperties)
        }
      }
      .outputMode("update")
      .start()
  }
}
