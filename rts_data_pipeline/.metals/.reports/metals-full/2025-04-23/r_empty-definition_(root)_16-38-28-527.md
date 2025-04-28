error id: io/github/bendyland/rtsdatapipeline/utils/SparkSessionFactory.create().
file://<WORKSPACE>/src/main/scala/io/github/bendyland/rtsdatapipeline/Main.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol io/github/bendyland/rtsdatapipeline/utils/SparkSessionFactory.create().
empty definition using fallback
non-local guesses:

offset: 329
uri: file://<WORKSPACE>/src/main/scala/io/github/bendyland/rtsdatapipeline/Main.scala
text:
```scala
package io.github.bendyland.rtsdatapipeline

import io.github.bendyland.rtsdatapipeline.utils.SparkSessionFactory
import io.github.bendyland.rtsdatapipeline.jobs.SensorStreamJob
import io.github.bendyland.rtsdatapipeline.sinks.DataSink

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSessionFactory.@@create("KafkaConsumerApp")
    val mode = args.headOption.map(_.toLowerCase)

    mode match {
      case Some("show") => SensorStreamJob.show(spark)
      case Some("combine") => 
        DataSink.combineParquets("data/parquet/enriched", "data/parquet/enriched_combined")(spark)
        DataSink.combineParquets("data/parquet/aggregated", "data/parquet/aggregated_combined")(spark)
      case Some(invalid) =>
        println(s"Unknown mode: '$invalid'. Supported: 'show', 'combine'")
        sys.exit(1)
      case None => SensorStreamJob.run(spark)
    }
  }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 