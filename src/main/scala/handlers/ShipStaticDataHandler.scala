package esgi.datastreming.org
package handlers

import config.ConfigLoader

import esgi.datastreming.org.database.Schemas.ShipStaticDataSchema
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.{DataFrame, functions => F}
import org.apache.spark.sql.streaming.StreamingQuery

import java.util.Properties

object ShipStaticDataHandler extends MessageHandler {

  override def messageType: String = "ShipStaticData"

  override def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery = {
    val withParsedMeta = jsonDf
      .withColumn("Message", from_json(col("Message"), ShipStaticDataSchema))

    val filteredDf = withParsedMeta.filter(F.col("Message.MessageType") === messageType)

    // Parse the ShipStaticData fields and compute length/width
    val parsedDf = filteredDf.select(
        F.col("Message.MetaData.MMSI").as("MMSI"),
        F.trim(F.col("Message.MetaData.ShipName")).as("ShipName"),
        F.col("Message.Message.ShipStaticData.ImoNumber").as("ImoNumber"),
        F.col("Message.Message.ShipStaticData.MaximumStaticDraught").as("MaximumStaticDraught"),
        F.col("Message.Message.ShipStaticData.Dimension.A").as("A"),
        F.col("Message.Message.ShipStaticData.Dimension.B").as("B"),
        F.col("Message.Message.ShipStaticData.Dimension.C").as("C"),
        F.col("Message.Message.ShipStaticData.Dimension.D").as("D")
      )
      .withColumn("length", F.col("A") + F.col("B"))
      .withColumn("width", F.col("C") + F.col("D"))
      .drop("A", "B", "C", "D")

    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        if (!batchDf.isEmpty) {
          val spark = batchDf.sparkSession

          // Get distinct ships in the current batch
          val distinctShips = batchDf
            .filter("ImoNumber IS NOT NULL AND ImoNumber > 0") // Ensure valid ImoNumber
            .select("ImoNumber", "MMSI", "ShipName", "MaximumStaticDraught", "length", "width")
            .distinct()

          // Read existing ships from DB
          val shipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)

          // Identify new ships that are not in DB yet (match by ImoNumber)
          val newShips = distinctShips.join(
            shipsDf,
            Seq("ImoNumber"),
            "left_anti"
          )

          // Insert new ships into 'ships' table
          if (!newShips.isEmpty) {
            newShips.write
              .mode("append")
              .jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
