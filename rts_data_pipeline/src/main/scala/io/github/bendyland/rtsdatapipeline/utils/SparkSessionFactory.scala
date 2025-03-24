package io.github.bendyland.rtsdatapipeline.utils

import org.apache.spark.sql.SparkSession

object SparkSessionFactory {
  def create(appName: String): SparkSession = {
    SparkSession.builder()
      .appName(appName)
      .master("local[*]")
      .getOrCreate()
  }
}

