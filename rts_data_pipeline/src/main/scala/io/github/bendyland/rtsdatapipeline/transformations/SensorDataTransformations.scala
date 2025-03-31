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
    // val aqStream = enrichedDF
    //   .filter($"SensorType" === "air_quality" && $"timestamp".isNotNull)
    //   .withWatermark("timestamp", "5 minutes")
    //   .withColumn("window", window($"timestamp", "2 minute"))
    //   .select(
    //     $"window",
    //     $"SensorId",
    //     $"CO".as("aq_CO") 
    //   )
    //   .alias("aq")

    // // Prepare the weather stream similarly:
    // val wStream = enrichedDF
    //   .filter($"SensorType" === "weather" && $"timestamp".isNotNull)
    //   .withWatermark("timestamp", "5 minutes")
    //   .withColumn("window", window($"timestamp", "2 minute"))
    //   .select(
    //     $"window",
    //     $"SensorId",
    //     $"temperature".as("w_temperature") 
    //   )
    //   .alias("w")

    // // Join on the common "window" column:
    // // val joinedDF = aqStream.join(wStream, Seq("window"))
    // val joinedDF = aqStream.join(wStream, Seq("window", "SensorId"))

    // // Now perform aggregations using the disambiguated column names:
    // joinedDF.groupBy($"window")
    //   .agg(
    //     avg($"aq_CO").as("avg_CO"),           
    //     avg($"w_temperature").as("avg_temperature") 
    //   )
    import org.apache.spark.sql.functions._

    val aqStream = enrichedDF
      .filter($"SensorType" === "air_quality" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "10 minutes")
      .withColumn("window", window($"timestamp", "2 minutes", "1 minute"))
      .withColumn("lat", round($"latitude", 4))
      .withColumn("lon", round($"longitude", 4))
      .select($"window", $"lat", $"lon", $"CO")
      .alias("aq")

    val wStream = enrichedDF
      .filter($"SensorType" === "weather" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "10 minutes")
      .withColumn("window", window($"timestamp", "2 minutes", "1 minute"))
      .withColumn("lat", round($"latitude", 4))
      .withColumn("lon", round($"longitude", 4))
      .select($"window", $"lat", $"lon", $"temperature")
      .alias("w")

    val joinedDF = aqStream.join(wStream, Seq("window", "lat", "lon"))

    joinedDF.groupBy("window", "lat", "lon")
      .agg(
        avg($"CO").as("avg_CO"),
        avg($"temperature").as("avg_temperature")
      )
  }

  // Enrich the parsed SensorData by converting the timestamp string and extracting fields from the maps.
  def enrichSensorData(sensorDS: Dataset[SensorData])(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    sensorDS.toDF()
      // Convert the timestamp string into a proper timestamp
      .withColumn("timestamp", to_timestamp($"Timestamp", "yyyy-MM-dd'T'HH:mm:ssX"))
      // Extract and cast weather readings
      .withColumn("temperature", $"Readings".getItem("temperature").cast("double"))
      .withColumn("humidity", $"Readings".getItem("humidity").cast("int"))
      .withColumn("wind_speed", $"Readings".getItem("wind_speed").cast("double"))
      // Extract and cast air quality readings
      .withColumn("CO", $"Readings".getItem("CO").cast("int"))
      .withColumn("NO2", $"Readings".getItem("NO2").cast("int"))
      .withColumn("PM10", $"Readings".getItem("PM10").cast("int"))
      .withColumn("PM2_5", $"Readings".getItem("PM2_5").cast("int"))
      .withColumn("ozone", $"Readings".getItem("ozone").cast("int"))
      // Extract location coordinates 
      .withColumn("latitude", $"Location".getItem("latitude").cast("double"))
      .withColumn("longitude", $"Location".getItem("longitude").cast("double"))
  }
}

