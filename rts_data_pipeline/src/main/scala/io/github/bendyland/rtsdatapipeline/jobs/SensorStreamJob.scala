package io.github.bendyland.rtsdatapipeline.jobs

import org.apache.spark.sql.{SparkSession, Dataset}
import io.github.bendyland.rtsdatapipeline.transformations.SensorDataTransformations
import io.github.bendyland.rtsdatapipeline.sinks.ConsoleWriter
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

    val messagesDS = kafkaDF.selectExpr("CAST(value AS STRING) as message")
      .as[String]

    val sensorDataDS = SensorDataTransformations.parseSensorData(messagesDS)(spark)
    // val dangerousSensors = SensorDataTransformations.filterDangerous(sensorDataDS)

    ConsoleWriter.write(sensorDataDS).awaitTermination()
  }
}
