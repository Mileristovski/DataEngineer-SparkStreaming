package esgi.datastreming.org
package database

import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

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
}
