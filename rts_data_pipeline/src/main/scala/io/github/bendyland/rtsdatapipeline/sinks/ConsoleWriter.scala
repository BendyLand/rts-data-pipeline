package io.github.bendyland.rtsdatapipeline.sinks

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.streaming.StreamingQuery

object ConsoleWriter {
  def write(ds: Dataset[_], outputMode: String): StreamingQuery = {
    ds.writeStream
      .outputMode(outputMode)
      .format("console")
      .start()
  }
}
