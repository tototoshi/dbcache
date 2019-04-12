name := """dbcache"""

lazy val mysqlDependency = "mysql" % "mysql-connector-java" % "8.0.15"
lazy val postgresqlDependency = "org.postgresql" % "postgresql" % "42.2.5"

lazy val testDependencies = Seq(
  "org.flywaydb" % "flyway-core" % "5.2.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)

lazy val commonSettings = Seq(
  organization := "com.github.tototoshi",
  scalaVersion := "2.11.8",
  version := "0.2.0"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(Publish.nonPublishSettings)
  .settings(
    name := "dbcache"
  )
  .aggregate(core, mysql, postgresql)

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(
    name := "dbcache-core",
    libraryDependencies ++= testDependencies
)

lazy val mysql = (project in file("mysql"))
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(
    name := "dbcache-mysql",
    libraryDependencies ++= testDependencies ++ Seq(
      mysqlDependency % "provided"
    )
).dependsOn(core)

lazy val postgresql = (project in file("postgresql"))
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(
    name := "dbcache-postgresql",
    libraryDependencies ++= testDependencies ++ Seq(
      postgresqlDependency % "provided"
    )
).dependsOn(core)

lazy val example = (project in file("example"))
  .settings(commonSettings)
  .settings(Publish.nonPublishSettings)
  .settings(
    name := "dbcache-example",
    libraryDependencies ++= testDependencies ++ Seq(
      mysqlDependency,
      postgresqlDependency
    )
).dependsOn(mysql, postgresql)

lazy val examplePlay = (project in file("example-play"))
  .enablePlugins(PlayScala)
  .settings(commonSettings)
  .settings(Publish.nonPublishSettings)
  .settings(
    name := "dbcache-example-play",
    libraryDependencies ++= Seq(
      cache,
      mysqlDependency,
      postgresqlDependency,
      "org.scalikejdbc" %% "scalikejdbc" % "2.3.5",
      "org.scalikejdbc" %% "scalikejdbc-config" % "2.3.5",
      "org.flywaydb" %% "flyway-play" % "2.3.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    ),
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).dependsOn(mysql, postgresql)
