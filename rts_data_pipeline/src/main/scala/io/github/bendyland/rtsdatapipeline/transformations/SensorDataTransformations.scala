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



  // // This function takes your enriched DataFrame and produces an aggregated result after joining.
  // def preJoinAndAggregate(enrichedDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
  //   import spark.implicits._
  //   // Rename columns before join
  //   val aqStream = enrichedDF
  //     .filter($"SensorType" === "air_quality" && $"timestamp".isNotNull)
  //     .withWatermark("timestamp", "2 minutes")
  //     .withColumn("window", window($"timestamp", "1 minute"))
  //     .withColumnRenamed("CO", "aq_CO")
  //     .alias("aq")

  //   val wStream = enrichedDF
  //     .filter($"SensorType" === "weather" && $"timestamp".isNotNull)
  //     .withWatermark("timestamp", "2 minutes")
  //     .withColumn("window", window($"timestamp", "1 minute"))
  //     .withColumnRenamed("temperature", "w_temperature")
  //     .alias("w")

  //   // Perform the join
  //   val joinedDF = aqStream.join(wStream, Seq("window"))

  //   // Explicitly select the columns you want to keep, using the air_quality value for CO:
  //   val resolvedDF = joinedDF.select(
  //     $"window",
  //     $"aq.SensorId".as("aq_SensorId"),
  //     $"w.SensorId".as("w_SensorId"),
  //     $"aq.aq_CO".as("CO"),  // Use the air_quality CO
  //     $"w.w_temperature".as("w_temperature")
  //     // Include other columns as needed.
  //   )

  //   val aggregatedJoined = resolvedDF.groupBy($"window")
  //     .agg(
  //       avg($"aq_CO").as("avg_CO"),
  //       avg($"w_temperature").as("avg_temperature")
  //     )
  //   aggregatedJoined    
  // }


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

  // def enrichSensorData(sensorDS: Dataset[SensorData])(implicit spark: SparkSession): DataFrame = {
  //   import spark.implicits._
  //   sensorDS.toDF()
  //     // Convert the string timestamp into a Spark timestamp type.
  //     .withColumn("timestamp", to_timestamp($"Timestamp", "yyyy-MM-dd'T'HH:mm:ssX"))
  //     // Extract common air quality readings
  //     .withColumn("CO", $"Readings".getItem("CO"))
  //     .withColumn("NO2", $"Readings".getItem("NO2"))
  //     .withColumn("PM10", $"Readings".getItem("PM10"))
  //     .withColumn("PM2_5", $"Readings".getItem("PM2_5"))
  //     .withColumn("ozone", $"Readings".getItem("ozone"))
  //     // Extract weather readings (if available)
  //     .withColumn("temperature", $"Readings".getItem("temperature"))
  //     .withColumn("humidity", $"Readings".getItem("humidity"))
  //     .withColumn("wind_speed", $"Readings".getItem("wind_speed"))
  //     // Extract location coordinates
  //     .withColumn("latitude", $"Location".getItem("latitude"))
  //     .withColumn("longitude", $"Location".getItem("longitude"))
  // }

  def aggregateAirQuality(enrichedDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    // Filter, set watermark, and group by the built-in window (which uses "timestamp")
    val airQualityDF = enrichedDF
      .filter($"SensorType" === "air_quality" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "2 minutes")

    airQualityDF
      .groupBy(window($"timestamp", "1 minute"), $"SensorId")
      .agg(
        avg($"CO").as("avg_CO"),
        min($"CO").as("min_CO"),
        max($"CO").as("max_CO"),
        avg($"NO2").as("avg_NO2"),
        min($"NO2").as("min_NO2"),
        max($"NO2").as("max_NO2"),
        avg($"PM10").as("avg_PM10"),
        min($"PM10").as("min_PM10"),
        max($"PM10").as("max_PM10")
      )
  }

  def aggregateWeather(enrichedDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    // Filter, set watermark, and group by the built-in window (using the same "timestamp")
    val weatherDF = enrichedDF
      .filter($"SensorType" === "weather" && $"timestamp".isNotNull)
      .withWatermark("timestamp", "2 minutes")

    // For simplicity, you could aggregate weather data using a function like first() for each field
    weatherDF
      .groupBy(window($"timestamp", "1 minute"), $"SensorId")
      .agg(
        first($"temperature").as("temperature"),
        first($"humidity").as("humidity"),
        first($"wind_speed").as("wind_speed"),
        first($"latitude").as("latitude"),
        first($"longitude").as("longitude")
      )
  }

  // Join aggregated air quality data with weather data on the common window.
  // In a production system, you might also join on a region (e.g. using a rounded version of latitude/longitude).
  def joinAirQualityAndWeather(airQualityAggDF: DataFrame, weatherAggDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    // Join on the "window" column (the struct with start and end)
    airQualityAggDF.join(weatherAggDF, Seq("window"))
  }

  // Flag dangerous conditions based on arbitrary thresholds (you can tune these as needed).
  def flagDangerousConditions(enrichedDF: DataFrame)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    enrichedDF.withColumn("is_dangerous", 
      when($"CO" > 30 || $"NO2" > 80, true).otherwise(false)
    )
  }

  // The pre‐existing dangerous filter for SensorData objects.
  def filterDangerous(ds: Dataset[SensorData]): Dataset[SensorData] = {
    ds.filter(_.Status == "DANGEROUS")
  }
}



// package io.github.bendyland.rtsdatapipeline.transformations

// import org.apache.spark.sql.{Dataset, SparkSession}
// import io.github.bendyland.rtsdatapipeline.messageparser.{SensorData, MessageParser}

// object SensorDataTransformations {
//   def parseSensorData(jsonDS: Dataset[String])(implicit spark: SparkSession): Dataset[SensorData] = {
//     import spark.implicits._
//     jsonDS.flatMap(MessageParser.parseSensorData)
//   }

//   def filterDangerous(ds: Dataset[SensorData]): Dataset[SensorData] = {
//     ds.filter(_.Status == "DANGEROUS")
//   }
// }



