package esgi.datastreming.org
package handlers

import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, functions => F}

object PositionReportHandler extends MessageHandler {
  override def messageType: String = "PositionReport"

  override def handle(jsonDf: DataFrame): StreamingQuery = ???

  override def initialParsing(df: DataFrame): DataFrame = ???
}
