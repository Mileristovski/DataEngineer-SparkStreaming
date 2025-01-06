package esgi.datastreaming.org
package handlers

import config.ConfigLoader
import messages.Schemas.metaDataSchema
import kafka.Kafka.writeKafkaTopic
import kafka.Schemas.positionSchema

import org.apache.avro.generic.GenericData
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.StreamingQuery

object MetaDataHandler extends MessageHandler {
  override def messageType: String = ""

  override def initialParsing(df: DataFrame): DataFrame = {
    df.select(
      col("MetaData.MetaData.MMSI").as("ShipMMSI"),
      col("MetaData.MetaData.latitude").as("Latitude"),
      col("MetaData.MetaData.longitude").as("Longitude")
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
          // Transform DataFrame rows to Avro records
          val schema = positionSchema()
          val avroRecords = batchDf.collect().map { row =>
            val record = new GenericData.Record(schema)
            record.put("ShipMMSI", row.getAs[Long]("ShipMMSI"))
            record.put("Latitude", row.getAs[Double]("Latitude"))
            record.put("Longitude", row.getAs[Double]("Longitude"))

            // Use ShipMMSI as the key
            val key = row.getAs[Long]("ShipMMSI").toString

            (record, key)
          }

          // Send Avro records to Kafka
          avroRecords.foreach { case (record, key) =>
            writeKafkaTopic(record, ConfigLoader.Kafka.positions, key)
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
