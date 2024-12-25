package esgi.datastreming.org
package handlers

import config.ConfigLoader
import database.Schemas.ShipStaticDataSchema

import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, functions => F}

import kafka.Kafka.writeKafkaStream

import java.util.Properties

object ShipStaticDataHandler extends MessageHandler {

  override def messageType: String = "ShipStaticData"

  override def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery = {
    val withParsedMeta = jsonDf
      .withColumn("Message", from_json(col("Message"), ShipStaticDataSchema))

    val filteredDf = withParsedMeta.filter(F.col("Message.MessageType") === messageType)
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

          val distinctShips = batchDf
            .filter(col("ImoNumber").isNotNull && col("ImoNumber") > 0)
            .select("ImoNumber", "MMSI", "ShipName", "MaximumStaticDraught", "length", "width")
            .distinct()

          val shipsDf = spark.read.jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)
          val newShips = distinctShips.join(
            shipsDf,
            Seq("ImoNumber"),
            "left_anti"
          )

          if (!newShips.isEmpty) {
            writeKafkaStream(newShips)
//            newShips.write
//              .mode("append")
//              .jdbc(ConfigLoader.DbConfig.jdbc, "ships", connectionProperties)
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
