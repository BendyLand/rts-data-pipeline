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

    // Get the final aggregated DataFrame from our transformation function
    val finalDF = SensorDataTransformations.preJoinAndAggregate(enrichedDF)(spark)

    // Write enriched data to Parquet
    // DataSink.writeParquet(enrichedDF, "data/parquet/enriched", "checkpoints/enriched")
    DataSink.readParquetAndWriteToConsole("data/parquet/enriched")(spark)

    // Write aggregated data to Parquet
    // DataSink.writeParquetAndAwaitTermination(finalDF, "data/parquet/aggregated", "checkpoints/aggregated")
    DataSink.readParquetAndWriteToConsole("data/parquet/aggregated")(spark)
  }
}
