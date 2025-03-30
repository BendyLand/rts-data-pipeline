error id: 9DF73394F9C901749559B08F7AC0D790
file://<WORKSPACE>/build.sbt
### java.lang.AssertionError: assertion failed: file://<WORKSPACE>/build.sbt: 2274 >= 2274

occurred in the presentation compiler.



action parameters:
offset: 2274
uri: file://<WORKSPACE>/build.sbt
text:
```scala
// give the user a nice default project!

val sparkVersion = settingKey[String]("Spark version")

lazy val root = (project.in(file(".")).

  settings(
    inThisBuild(List(
      organization := "io.github.bendyland",
      scalaVersion := "2.12.20"
    )),

    ThisBuild / semanticdbEnabled := true,
    ThisBuild / semanticdbVersion := "4.8.11",

    name := "rts-data-pipeline",
    version := "0.0.1",

    sparkVersion := "3.3.0",

    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    parallelExecution / Test := false,
    fork := true,

    coverageHighlighting := true,

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-streaming" % "3.3.0" % "provided",
      "org.apache.spark" %% "spark-sql" % "3.3.0" % "provided",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.1",

      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",

      "org.scalatest" %% "scalatest" % "3.2.2" % "test",
      "org.scalacheck" %% "scalacheck" % "1.15.2" % "test",
      "com.holdenkarau" %% "spark-testing-base" % "3.3.0_1.3.0" % "test"
    ),

    // uses compile classpath for the run task, including "provided" jar (cf http://stackoverflow.com/a/21803413/3827)
    run / Compile := Defaults.runTask(fullClasspath / Compile, mainClass / (Compile, run), runner / (Compile, run)).evaluated,

    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    pomIncludeRepository := { x => false },

   resolvers ++= Seq(
      "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
      "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "https://repo.typesafe.com/typesafe/maven-releases/",
      Resolver.sonatypeOssRepos("snapshots")
    ),

    pomIncludeRepository := { _ => false },

    // publish settings
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )
@@
```


presentation compiler configuration:
Scala version: 2.12.20
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.20/scala-library-2.12.20.jar [exists ]
Options:





#### Error stacktrace:

```
scala.reflect.internal.util.SourceFile.position(SourceFile.scala:33)
	scala.tools.nsc.CompilationUnits$CompilationUnit.position(CompilationUnits.scala:133)
	scala.meta.internal.pc.SignatureHelpProvider.signatureHelp(SignatureHelpProvider.scala:25)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$signatureHelp$1(ScalaPresentationCompiler.scala:421)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: file://<WORKSPACE>/build.sbt: 2274 >= 2274