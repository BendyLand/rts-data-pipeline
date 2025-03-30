addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

// Metals (provides metalsSetup, bloopInstall, etc.)
addSbtPlugin("org.scalameta" % "sbt-metals" % "0.11.12")

// Optional: sbt plugin for Bloop integration
addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.5.14")


