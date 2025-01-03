package esgi.datastreaming.org
package kafka

import org.apache.avro.Schema

object Schemas {
  def shipSchema(): Schema = {
    val shipRecordSchema =
      """{
        |"type": "record",
        |"name": "shiprecord",
        |"fields": [
        |  {"name": "ImoNumber", "type": "long"},
        |  {"name": "MMSI", "type": "long"},
        |  {"name": "ShipName", "type": "string"},
        |  {"name": "MaximumStaticDraught", "type": "double"},
        |  {"name": "Length", "type": "long"},
        |  {"name": "Width", "type": "long"}
        |]
        |}""".stripMargin

    val parser = new Schema.Parser()
    parser.parse(shipRecordSchema)
  }

  def positionSchema(): Schema = {
    val shipPositionSchema =
      """{
        |"type": "record",
        |"name": "shipposition",
        |"fields": [
        |  {"name": "ShipMMSI", "type": "long"},
        |  {"name": "Latitude", "type": "double"},
        |  {"name": "Longitude", "type": "double"}
        |]
        |}""".stripMargin

    val parser = new Schema.Parser()
    parser.parse(shipPositionSchema)
  }
}
