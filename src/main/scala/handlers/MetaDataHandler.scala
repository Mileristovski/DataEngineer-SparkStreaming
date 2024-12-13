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
    val withParsedMeta = jsonDf
      .withColumn("MetaData", from_json(col("Message"), metaDataSchema))

    val parsedDf = withParsedMeta.select(
        col("MetaData.MetaData.MMSI").as("MMSI"),
        col("MetaData.MetaData.ShipName").as("ShipName"),
        col("MetaData.MetaData.latitude").as("latitude"),
        col("MetaData.MetaData.longitude").as("longitude")
      )

    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        val spark = batchDf.sparkSession
        val shipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)
        val batchWithShipId = batchDf.join(shipsDf, Seq("MMSI", "ShipName"), "inner")
          .select(
            col("id").as("ship_id"),
            col("latitude"),
            col("longitude")
          )

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
