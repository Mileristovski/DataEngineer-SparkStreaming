package esgi.datastreming.org
package handlers

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

import java.util.Properties

trait MessageHandler {
  def messageType: String
  def handle(jsonDf: DataFrame, connectionProperties: Properties): StreamingQuery
}