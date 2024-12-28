package esgi.datastreming.org
package handlers

import config.ConfigLoader
import database.Schemas.metaDataSchema
import kafka.Kafka.writeKafkaTopic

import org.apache.spark.sql.functions.{col, from_json, lit}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, functions => F}

object MetaDataHandler extends MessageHandler {
  override def messageType: String = ""

  override def initialParsing(df: DataFrame): DataFrame = {
    df.select(
      col("MetaData.MetaData.MMSI").as("ShipMMSI"),
      col("MetaData.MetaData.latitude").as("Latitude"),
      col("MetaData.MetaData.longitude").as("Longitude")
    )
  }

  override def jsonSchema(df: DataFrame): DataFrame = {
    df.select(
      col("ShipMMSI").cast("string").as("key"), // Key as string
      F.to_json(
        F.struct(
          F.struct(
            lit("struct").as("type"),
            F.array(
              F.struct(lit("ShipMMSI").as("field"), lit("int64").as("type")),
              F.struct(lit("Latitude").as("field"), lit("double").as("type")),
              F.struct(lit("Longitude").as("field"), lit("double").as("type"))
            ).as("fields")
          ).as("schema"),
          F.struct(
            col("ShipMMSI"),
            col("Latitude"),
            col("Longitude")
          ).as("payload")
        )
      ).as("value")
    )
  }

  override def handle(jsonDf: DataFrame): StreamingQuery = {
    val rawData = jsonDf
      .withColumn("MetaData", from_json(col("Message"), metaDataSchema))

    // Get the useful data from the raw data
    val parsedDf = initialParsing(rawData)

    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        if (!batchDf.isEmpty) {
          val kafkaDf = jsonSchema(batchDf)
          writeKafkaTopic(kafkaDf, ConfigLoader.Kafka.positions)
        }
      }
      .outputMode("update")
      .start()
  }
}
