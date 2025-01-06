package esgi.datastreaming.org
package messages

import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType}
import org.apache.spark.sql.types._

object Schemas {
  val metaDataSchema: StructType = StructType(Seq(
    StructField("MetaData", StructType(Seq(
      StructField("MMSI", IntegerType),
      StructField("MMSI_String", StringType),
      StructField("ShipName", StringType),
      StructField("latitude", DoubleType),
      StructField("longitude", DoubleType),
      StructField("time_utc", StringType)
    )))
  ))

  val ShipStaticDataSchema: StructType = StructType(Seq(
    StructField("MessageType", StringType),
    StructField("Message", StructType(Seq(
      StructField("ShipStaticData", StructType(Seq(
        StructField("ImoNumber", LongType),
        StructField("MaximumStaticDraught", DoubleType),
        StructField("Dimension", StructType(Seq(
          StructField("A", DoubleType),
          StructField("B", DoubleType),
          StructField("C", DoubleType),
          StructField("D", DoubleType)
        )))
      )))
    ))),
    StructField("MetaData", StructType(Seq(
      StructField("MMSI", LongType),
      StructField("ShipName", StringType),
      StructField("latitude", DoubleType),
      StructField("longitude", DoubleType)
    )))
  ))
}
