package esgi.datastreming.org
package handlers

import config.ConfigLoader
import database.Schemas.ShipStaticDataSchema
import kafka.Kafka.writeKafkaTopic

import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, functions => F}

object ShipStaticDataHandler extends MessageHandler {

  override def messageType: String = "ShipStaticData"

  override def initialParsing(df: DataFrame): DataFrame = {
    df.select(
      F.col("Message.MetaData.MMSI").as("MMSI"),
      F.trim(F.col("Message.MetaData.ShipName")).as("ShipName"),
      F.col("Message.Message.ShipStaticData.ImoNumber").as("ImoNumber"),
      F.col("Message.Message.ShipStaticData.MaximumStaticDraught").as("MaximumStaticDraught"),
      F.col("Message.Message.ShipStaticData.Dimension.A").as("A"),
      F.col("Message.Message.ShipStaticData.Dimension.B").as("B"),
      F.col("Message.Message.ShipStaticData.Dimension.C").as("C"),
      F.col("Message.Message.ShipStaticData.Dimension.D").as("D")
    )
  }

  override def jsonSchema(df: DataFrame): DataFrame = {
    df.select(
      col("ShipName").cast("string").as("key"), // Key as string
      F.to_json(
        F.struct(
          F.struct(
            lit("struct").as("type"),
            F.array(
              F.struct(lit("ImoNumber").as("field"), lit("int64").as("type")),
              F.struct(lit("MMSI").as("field"), lit("int64").as("type")),
              F.struct(lit("ShipName").as("field"), lit("string").as("type")),
              F.struct(lit("MaximumStaticDraught").as("field"), lit("double").as("type")),
              F.struct(lit("Length").as("field"), lit("int64").as("type")),
              F.struct(lit("Width").as("field"), lit("int64").as("type"))
            ).as("fields")
          ).as("schema"),
          F.struct(
            col("ImoNumber"),
            col("MMSI"),
            col("ShipName"),
            col("MaximumStaticDraught"),
            col("Length"),
            col("Width")
          ).as("payload")
        )
      ).as("value")
    )
  }

  override def handle(jsonDf: DataFrame): StreamingQuery = {
    val rawData = jsonDf
      .withColumn("Message", from_json(col("Message"), ShipStaticDataSchema))

    val filteredDf = rawData.filter(F.col("Message.MessageType") === messageType)

    // Get the useful data from the raw data
    val parsedDf = initialParsing(filteredDf)

    val transformedDf = parsedDf
      .withColumn("Length", F.col("A") + F.col("B"))
      .withColumn("Width", F.col("C") + F.col("D"))
      .drop("A", "B", "C", "D")

    transformedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        if (!batchDf.isEmpty) {
          val distinctShips = batchDf
            .filter(col("ImoNumber").isNotNull && col("ImoNumber") > 0)
            .distinct()

          if (!distinctShips.isEmpty) {
            // Transform the DataFrame to include schema and payload
            val keyValueDf = jsonSchema(distinctShips)
            writeKafkaTopic(keyValueDf, ConfigLoader.Kafka.ships)
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
