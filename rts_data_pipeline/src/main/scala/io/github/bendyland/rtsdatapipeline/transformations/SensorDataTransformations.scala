package io.github.bendyland.rtsdatapipeline.transformations

import org.apache.spark.sql.{Dataset, SparkSession}
import io.github.bendyland.rtsdatapipeline.messageparser.{SensorData, MessageParser}

object SensorDataTransformations {
  def parseSensorData(jsonDS: Dataset[String])(implicit spark: SparkSession): Dataset[SensorData] = {
    import spark.implicits._
    jsonDS.flatMap(MessageParser.parseSensorData)
  }

  def filterDangerous(ds: Dataset[SensorData]): Dataset[SensorData] = {
    ds.filter(_.Status == "DANGEROUS")
  }
}



