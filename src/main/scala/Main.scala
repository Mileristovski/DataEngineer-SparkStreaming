package esgi.datastreming.org

import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties")

    val spark = SparkSession.builder()
      .appName("spark-video-course")
      .master("local[*]")
      .config("spark.driver.bindAddress", "127.0.0.1")
      .getOrCreate()

    val df = spark.read
      .option("header", value = true)
      .option("inferSchema", value = true)
      .csv("data/AAPL.csv")


    df.show()
    df.printSchema()
  }
}
