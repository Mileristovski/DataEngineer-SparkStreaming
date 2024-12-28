package esgi.datastreming.org
package handlers

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

trait MessageHandler {
  def messageType: String
  def initialParsing(df: DataFrame): DataFrame
  def jsonSchema(df: DataFrame): DataFrame
  def handle(jsonDf: DataFrame): StreamingQuery
}