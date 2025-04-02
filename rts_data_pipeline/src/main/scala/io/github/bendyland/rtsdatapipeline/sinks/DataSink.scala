package io.github.bendyland.rtsdatapipeline.sinks

import org.apache.spark.sql.{Dataset, SparkSession, DataFrame}
import org.apache.spark.sql.streaming.StreamingQuery

object DataSink {
  // Writes streaming or batch Dataset to the console
  def write(ds: Dataset[_], outputMode: String = "append"): Option[StreamingQuery] = {
    if (ds.isStreaming) {
      Some(
        ds.writeStream
          .outputMode(outputMode)
          .format("console")
          .option("truncate", false)
          .start()
      )
    } 
    else {
      ds.show(50, truncate = false)
      None
    }
  }

  // Reads a Parquet file from a given path
  def readParquet(path: String)(implicit spark: SparkSession): DataFrame = {
    spark.read.parquet(path)
  }

  // Reads a Parquet file and writes it to the console
  def readParquetAndWriteToConsole(path: String)(implicit spark: SparkSession): Unit = {
    val df = readParquet(path)
    write(df)
  }

  def writeParquet(data: DataFrame, dataPath: String, checkpointPath: String) = {
    data.writeStream
      .format("parquet")
      .option("path", dataPath)
      .option("checkpointLocation", checkpointPath)
      .outputMode("append")
      .start()
  }

  def writeParquetAndAwaitTermination(data: DataFrame, dataPath: String, checkpointPath: String) = {
    println("Writing data...")
    data.writeStream
      .format("parquet")
      .option("path", dataPath)
      .option("checkpointLocation", checkpointPath)
      .outputMode("append")
      .start()
      .awaitTermination()
  }
}

