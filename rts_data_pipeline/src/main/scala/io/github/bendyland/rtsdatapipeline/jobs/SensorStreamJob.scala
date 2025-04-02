package io.github.bendyland.rtsdatapipeline.jobs

import org.apache.spark.sql.{SparkSession, DataFrame}
import io.github.bendyland.rtsdatapipeline.transformations.SensorDataTransformations
import io.github.bendyland.rtsdatapipeline.sinks.DataSink
import io.github.bendyland.rtsdatapipeline.messageparser.SensorData

object SensorStreamJob {
  def run(spark: SparkSession): Unit = {
    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._

    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "sensor-data")
      .option("startingOffsets", "earliest")
      .load()

    val messagesDS = kafkaDF.selectExpr("CAST(value AS STRING) as message").as[String]
    val sensorDataDS = SensorDataTransformations.parseSensorData(messagesDS)(spark)
    val enrichedDF = SensorDataTransformations.enrichSensorData(sensorDataDS)(spark)
    val finalDF = SensorDataTransformations.preJoinAndAggregate(enrichedDF)(spark)

    DataSink.writeParquet(enrichedDF, "data/parquet/enriched", "checkpoints/enriched")
    DataSink.writeParquetAndAwaitTermination(finalDF, "data/parquet/aggregated", "checkpoints/aggregated")
  }

  def show(spark: SparkSession): Unit = {
    spark.sparkContext.setLogLevel("WARN")
    println("Enriched data:")
    DataSink.readParquetAndWriteToConsole("data/parquet/enriched")(spark)

    println("Aggregated data:")
    DataSink.readParquetAndWriteToConsole("data/parquet/aggregated")(spark)
  }
}

