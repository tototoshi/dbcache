name := """dbcache"""

lazy val mysqlDependency = "mysql" % "mysql-connector-java" % "8.0.26"
lazy val postgresqlDependency = "org.postgresql" % "postgresql" % "42.2.23"

lazy val testDependencies = Seq(
  "org.flywaydb" % "flyway-core" % "7.12.0" % "test",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % "test"
)

lazy val commonSettings = Seq(
  organization := "com.github.tototoshi",
  scalacOptions ++= Seq("-deprecation"),
  scalaVersion := "2.12.13",
  crossScalaVersions := Seq("2.13.6", "2.12.13"),
  version := "0.4.1-SNAPSHOT"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(Publish.nonPublishSettings)
  .settings(
    name := "dbcache"
  )
  .aggregate(core, mysql, postgresql, play)

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

lazy val play = (project in file("play"))
  .settings(commonSettings)
  .settings(Publish.settings)
  .settings(
    name := "dbcache-play",
    libraryDependencies ++= testDependencies ++ Seq(
      cacheApi
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
      guice,
      mysqlDependency,
      postgresqlDependency,
      "org.scalikejdbc" %% "scalikejdbc" % "3.4.0",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.4.0",
      "org.flywaydb" %% "flyway-play" % "5.3.2"
    )
).dependsOn(mysql, postgresql, play)
