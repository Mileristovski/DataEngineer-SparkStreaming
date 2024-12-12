package esgi.datastreming.org
package handlers

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{functions => F}

import java.util.Properties

object PositionReportHandler extends MessageHandler {
  override def messageType: String = "PositionReport"

  override def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery = ???
}
