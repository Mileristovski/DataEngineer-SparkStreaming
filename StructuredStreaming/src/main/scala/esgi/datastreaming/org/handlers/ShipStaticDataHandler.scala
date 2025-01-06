package esgi.datastreaming.org
package handlers

import config.ConfigLoader
import messages.Schemas.ShipStaticDataSchema
import kafka.Kafka.writeKafkaTopic
import kafka.Schemas.shipSchema

import org.apache.avro.generic.GenericData
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
            // Transform DataFrame rows to Avro records
            val schema = shipSchema()
            val avroRecords = distinctShips.collect().map { row =>
              val record = new GenericData.Record(schema)
              record.put("ImoNumber", row.getAs[Long]("ImoNumber"))
              record.put("MMSI", row.getAs[Long]("MMSI"))
              record.put("ShipName", row.getAs[String]("ShipName"))
              record.put("MaximumStaticDraught", row.getAs[Double]("MaximumStaticDraught"))
              record.put("Length", row.getAs[Long]("Length"))
              record.put("Width", row.getAs[Long]("Width"))

              // Use ShipName as the key
              val key = row.getAs[String]("ShipName")

              (record, key)
            }

            // Send Avro records to Kafka
            avroRecords.foreach { case (record, key) =>
              writeKafkaTopic(record, ConfigLoader.Kafka.ships, key)
            }
          }
        }
      }
      .outputMode("update")
      .start()
  }
}
