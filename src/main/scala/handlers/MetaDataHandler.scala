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
    // Parse MetaData first
    val withParsedMeta = jsonDf
      .withColumn("MetaData", from_json(col("Message"), metaDataSchema))

    // Filter rows where "MetaData.MetaData.ShipName" is not null and not empty
    val filteredDf = withParsedMeta.filter(
      col("MetaData.MetaData.ShipName").isNotNull
        && col("MetaData.MetaData.ShipName") =!= ""
        && col("MetaData.MetaData.MMSI").isNotNull
        && col("MetaData.MetaData.MMSI_String").isNotNull
    )

    // Parsing the MetaData from the initial DataFrame
    val parsedDf = filteredDf.select(
        col("MetaData.MetaData.MMSI").as("MMSI"),
        col("MetaData.MetaData.ShipName").as("ShipName"),
        col("MetaData.MetaData.latitude").as("latitude"),
        col("MetaData.MetaData.longitude").as("longitude")
      )

    // Writing data to the Database
    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        val spark = batchDf.sparkSession

        // Distinct ships in current batch
        val distinctShipsDf = batchDf.select("MMSI", "ShipName").distinct()

        // Read existing ships from DB
        val shipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)

        // Identify new ships that are not in DB yet (left_anti join gives rows in distinctShipsDf not in shipsDf)
        val newShipsDf = distinctShipsDf.join(shipsDf, Seq("MMSI"), "left_anti")

        // Insert new ships into 'ships' table
        newShipsDf.write
          .mode("append")
          .jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)

        // Refresh the shipsDf after inserting new ships
        val updatedShipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)

        // Join batch data with updatedShipsDf to get ship_id
        // Assuming ships table has columns: id (ship_id), MMSI, ShipName
        val batchWithShipId = batchDf.join(updatedShipsDf, Seq("MMSI"), "left")
          .select(
            col("id").as("ship_id"),
            col("latitude"),
            col("longitude")
          )

        // Insert positions into ais_positions with foreign key ship_id
        batchWithShipId.write
          .mode("append")
          .jdbc(ConfigLoader.DbConfig.jdbc, "ais_positions", connectionProperties)
      }
      .outputMode("update")
      .start()
  }
}
