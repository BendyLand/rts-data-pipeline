package io.github.bendyland.rtsdatapipeline.transformations

import org.apache.spark.sql.{Dataset, DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import io.github.bendyland.rtsdatapipeline.messageparser.{SensorData, MessageParser}

object SensorDataTransformations {
  // Parse raw JSON strings into SensorData objects.
  def parseSensorData(jsonDS: Dataset[String])(implicit spark: SparkSession): Dataset[SensorData] = {
    import spark.implicits._
    jsonDS.flatMap(MessageParser.parseSensorData)
  }

  def preJoinAndAggregate(enrichedDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    // Prepare the air quality stream, selecting and renaming columns explicitly:
    val aqStream = enrichedDF
      .filter($"SensorType" === "air_quality" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "2 minutes")
      .withColumn("window", window($"timestamp", "1 minute"))
      .select(
        $"window",
        $"SensorId".as("aq_SensorId"),
        $"CO".as("aq_CO") // rename CO here
      )
      .alias("aq")

    // Prepare the weather stream similarly:
    val wStream = enrichedDF
      .filter($"SensorType" === "weather" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "2 minutes")
      .withColumn("window", window($"timestamp", "1 minute"))
      .select(
        $"window",
        $"SensorId".as("w_SensorId"),
        $"temperature".as("w_temperature") // rename temperature here
      )
      .alias("w")

    // Join on the common "window" column:
    val joinedDF = aqStream.join(wStream, Seq("window"))

    // Now perform your aggregation using the disambiguated column names:
    val aggregatedJoined = joinedDF.groupBy($"window")
      .agg(
        avg($"aq_CO").as("avg_CO"),           // Use the renamed air quality CO column
        avg($"w_temperature").as("avg_temperature") // Use the renamed weather temperature column
      )
    aggregatedJoined
  }

  // Enrich the parsed SensorData by converting the timestamp string and extracting fields from the maps.
  def enrichSensorData(sensorDS: Dataset[SensorData])(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    sensorDS.toDF()
      // Convert the timestamp string into a proper timestamp
      .withColumn("timestamp", to_timestamp($"Timestamp", "yyyy-MM-dd'T'HH:mm:ssX"))
      // Extract and cast common air quality readings to int
      .withColumn("CO", $"Readings".getItem("CO").cast("int"))
      .withColumn("NO2", $"Readings".getItem("NO2").cast("int"))
      .withColumn("PM10", $"Readings".getItem("PM10").cast("int"))
      .withColumn("PM2_5", $"Readings".getItem("PM2_5").cast("int"))
      .withColumn("ozone", $"Readings".getItem("ozone").cast("int"))
      // Extract and cast weather readings
      .withColumn("temperature", $"Readings".getItem("temperature").cast("double"))
      .withColumn("humidity", $"Readings".getItem("humidity").cast("int"))
      .withColumn("wind_speed", $"Readings".getItem("wind_speed").cast("double"))
      // Extract location coordinates (if needed)
      .withColumn("latitude", $"Location".getItem("latitude").cast("double"))
      .withColumn("longitude", $"Location".getItem("longitude").cast("double"))
  }
}

