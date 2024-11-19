package esgi.datastreming.org

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object StructuredStreaming {
  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties")

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .master("local[*]") // Specify the master
      .getOrCreate()

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "ais_data")
      .load()

    // Select and decode the 'value' column
    val jsonDf = df.selectExpr("CAST(value AS STRING) AS Message", "CAST(key AS STRING) as Key")

    // Start running the query that prints the parsed JSON data to the console
    val query = jsonDf.writeStream
      .outputMode("append")
      .format("console")
      .option("truncate", "false") // Prevent truncation of long rows
      .start()

    query.awaitTermination()
  }
}
