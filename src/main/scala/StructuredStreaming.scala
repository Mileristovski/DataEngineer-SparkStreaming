package esgi.datastreming.org

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object StructuredStreaming {
  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties")

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .master("local[*]")
      .getOrCreate()

    val metaDataSchema = StructType(Seq(
      StructField("MetaData", StructType(Seq(
        StructField("MMSI", IntegerType),
        StructField("MMSI_String", StringType),
        StructField("ShipName", StringType),
        StructField("latitude", DoubleType),
        StructField("longitude", DoubleType),
        StructField("time_utc", StringType)
      )))
    ))

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "ais_data")
      .load()

    val jsonDf = df.withColumn("Message", col("value").cast("string"))
    val parsedDf = jsonDf.withColumn("MetaData", from_json(col("Message"), metaDataSchema))
      .select(
        col("MetaData.MetaData.latitude").as("latitude"),
        col("MetaData.MetaData.longitude").as("longitude")
      )

    val jdbcUrl = "jdbc:postgresql://localhost:5432/ais_data"
    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", "postgres")
    connectionProperties.setProperty("password", "mysecretpassword")
    connectionProperties.setProperty("driver", "org.postgresql.Driver")

    // Writing data to the Database
    parsedDf.writeStream
      .foreachBatch { (batchDf: DataFrame, _: Long) =>
        batchDf.write
          .mode("append")
          .jdbc(jdbcUrl, "ais_positions", connectionProperties)
      }
      .outputMode("update")
      .start()
      .awaitTermination()
  }
}
