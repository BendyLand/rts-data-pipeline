package io.github.bendyland.rtsdatapipeline

import io.github.bendyland.rtsdatapipeline.utils.SparkSessionFactory
import io.github.bendyland.rtsdatapipeline.jobs.SensorStreamJob

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSessionFactory.create("KafkaConsumerApp")
    val mode = args.headOption.map(_.toLowerCase)

    mode match {
      case Some("show") => SensorStreamJob.show(spark)
      case Some(invalid) =>
        println(s"Unknown mode: '$invalid'. Supported: 'show'")
        sys.exit(1)
      case None => SensorStreamJob.run(spark)
    }
  }
}

