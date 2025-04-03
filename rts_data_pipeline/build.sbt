// give the user a nice default project!
ThisBuild / scalaVersion := "2.12.20"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.13.2"
ThisBuild / organization := "io.github.bendyland"

val sparkVersion = settingKey[String]("Spark version")

lazy val root = (project in file(".")).settings(
  name := "rts-data-pipeline",
  version := "0.0.1",

  sparkVersion := "3.3.0",

  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M"),
  scalacOptions ++= Seq("-deprecation", "-unchecked"),
  Test / parallelExecution := false,
  fork := true,

  coverageHighlighting := true,

  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion.value,
    "org.apache.spark" %% "spark-sql" % sparkVersion.value,
    "org.apache.spark" %% "spark-streaming" % sparkVersion.value,
    "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion.value,
    "org.apache.spark" %% "spark-token-provider-kafka-0-10" % sparkVersion.value,
    "org.apache.parquet" % "parquet-hadoop" % "1.12.3", // Add this
    "org.apache.kafka" % "kafka-clients" % "2.8.0",
    "io.circe" %% "circe-core" % "0.14.1",
    "io.circe" %% "circe-parser" % "0.14.1",
    "io.circe" %% "circe-generic" % "0.14.1",
    "org.scalatest" %% "scalatest" % "3.2.2" % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.2" % Test,
    "com.holdenkarau" %% "spark-testing-base" % "3.3.0_1.3.0" % Test,
  ),

  addCompilerPlugin(
    "org.scalameta" % "semanticdb-scalac" % "4.13.2" cross CrossVersion.full
  ),

  Compile / run := Defaults.runTask(Compile / fullClasspath, Compile / mainClass, Compile / runner).evaluated,

  pomIncludeRepository := { _ => false },

  resolvers ++= Resolver.sonatypeOssRepos("public"),

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
)

