error id: io/github/bendyland/rtsdatapipeline/jobs/SensorStreamJob.run().
file://<WORKSPACE>/src/main/scala/io/github/bendyland/rtsdatapipeline/Main.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -io/github/bendyland/rtsdatapipeline/jobs/SensorStreamJob.run.
	 -io/github/bendyland/rtsdatapipeline/jobs/SensorStreamJob.run#
	 -io/github/bendyland/rtsdatapipeline/jobs/SensorStreamJob.run().
	 -SensorStreamJob.run.
	 -SensorStreamJob.run#
	 -SensorStreamJob.run().
	 -scala/Predef.SensorStreamJob.run.
	 -scala/Predef.SensorStreamJob.run#
	 -scala/Predef.SensorStreamJob.run().
offset: 286
uri: file://<WORKSPACE>/src/main/scala/io/github/bendyland/rtsdatapipeline/Main.scala
text:
```scala
package io.github.bendyland.rtsdatapipeline

import io.github.bendyland.rtsdatapipeline.utils.SparkSessionFactory
import io.github.bendyland.rtsdatapipeline.jobs.SensorStreamJob

object Main extends App {
  val spark = SparkSessionFactory.create("KafkaConsumerApp")
  SensorStreamJob.ru@@n(spark)
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: 