package esgi.datastreming.org

import org.apache.spark.sql.SparkSession

object StructuredStreaming {
  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties")

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .master("local[*]") // Add this line to specify the master
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "Test")
      .load()

    // Start running the query that prints the running counts to the console
    val query = df.writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
