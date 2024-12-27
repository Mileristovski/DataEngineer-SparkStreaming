package esgi.datastreming.org
package handlers

import config.ConfigLoader
import database.Schemas.ShipStaticDataSchema
import kafka.Kafka.writeKafkaTopicShips

import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, functions => F}

import java.util.Properties

object ShipStaticDataHandler extends MessageHandler {

  override def messageType: String = "ShipStaticData"

  override def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery = {
    val withParsedMeta = jsonDf
      .withColumn("Message", from_json(col("Message"), ShipStaticDataSchema))

    val filteredDf = withParsedMeta.filter(F.col("Message.MessageType") === messageType)
    val parsedDf = filteredDf.select(
        F.col("Message.MetaData.MMSI").as("mmsi"),
        F.trim(F.col("Message.MetaData.ShipName")).as("shipname"),
        F.col("Message.Message.ShipStaticData.ImoNumber").as("imonumber"),
        F.col("Message.Message.ShipStaticData.MaximumStaticDraught").as("maximumstaticdraught"),
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
          val distinctShips = batchDf
            .filter(col("imonumber").isNotNull && col("imonumber") > 0)
            .select("imonumber", "mmsi", "shipname", "maximumstaticdraught", "length", "width")
            .distinct()

          if (!distinctShips.isEmpty) {
            // Transform the DataFrame to include schema and payload
            val keyValueDf = distinctShips.select(
              col("imonumber").cast("string").as("key"), // Key as string
              F.to_json(
                F.struct(
                  F.struct(
                    lit("struct").as("type"),
                    F.array(
                      F.struct(lit("imonumber").as("field"), lit("int64").as("type")),
                      F.struct(lit("mmsi").as("field"), lit("int64").as("type")),
                      F.struct(lit("shipname").as("field"), lit("string").as("type")),
                      F.struct(lit("maximumstaticdraught").as("field"), lit("double").as("type")),
                      F.struct(lit("length").as("field"), lit("int64").as("type")),
                      F.struct(lit("width").as("field"), lit("int64").as("type"))
                    ).as("fields")
                  ).as("schema"),
                  F.struct(
                    col("imonumber"),
                    col("mmsi"),
                    col("shipname"),
                    col("maximumstaticdraught"),
                    col("length"),
                    col("width")
                  ).as("payload")
                )
              ).as("value")
            )
            writeKafkaTopicShips(keyValueDf)
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
