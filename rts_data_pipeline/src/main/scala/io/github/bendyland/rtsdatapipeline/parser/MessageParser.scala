package io.github.bendyland.rtsdatapipeline.messageparser

import io.circe._, io.circe.parser._
import io.circe.generic.auto._

case class SensorData(
  Timestamp: String,
  SensorId: String,
  SensorType: String,
  Readings: Map[String, Double],
  Location: Map[String, Double],
  Status: String
)

object MessageParser {
  // Returns None if parsing failed
  def parseSensorData(jsonString: String): Option[SensorData] = {
    parse(jsonString).flatMap(_.as[SensorData]).toOption
  }
}
