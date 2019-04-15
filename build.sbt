name := """dbcache"""

lazy val mysqlDependency = "mysql" % "mysql-connector-java" % "8.0.15"
lazy val postgresqlDependency = "org.postgresql" % "postgresql" % "42.2.5"

lazy val testDependencies = Seq(
  "org.flywaydb" % "flyway-core" % "5.2.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.7" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
)

lazy val commonSettings = Seq(
  organization := "com.github.tototoshi",
  scalaVersion := "2.11.8",
  version := "0.3.0-SNAPSHOT"
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
      "org.scalikejdbc" %% "scalikejdbc" % "3.3.4",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.3.4",
      "org.flywaydb" %% "flyway-play" % "5.3.2"
    )
).dependsOn(mysql, postgresql)
