package io.github.bendyland.rtsdatapipeline

import io.github.bendyland.rtsdatapipeline.utils.SparkSessionFactory
import io.github.bendyland.rtsdatapipeline.jobs.SensorStreamJob

object Main extends App {
  val spark = SparkSessionFactory.create("KafkaConsumerApp")
  SensorStreamJob.run(spark)
}

