name := """dbcache"""

lazy val mysqlDependency = "com.mysql" % "mysql-connector-j" % "9.0.0"

lazy val postgresqlDependency = "org.postgresql" % "postgresql" % "42.7.3"
lazy val flywayVersion = "9.8.2"

lazy val testDependencies = Seq(
  "org.flywaydb" % "flyway-core" % flywayVersion % "test",
  "org.flywaydb" % "flyway-mysql" % flywayVersion % "test",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0" % "test"
)

lazy val commonSettings = Seq(
  organization := "com.github.tototoshi",
  scalacOptions ++= Seq("-deprecation"),
  scalaVersion := "2.13.14",
  crossScalaVersions := Seq("2.13.14", "3.3.3"),
  version := "1.0.0"
)

lazy val playScala3workaround = Def.settings(
  libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % "always",
  conflictWarning := {
    if (scalaBinaryVersion.value == "3") {
      ConflictWarning("warn", Level.Warn, false)
    } else {
      conflictWarning.value
    }
  },
  libraryDependencies ~= {
    _.map { x =>
      if (x.organization == "com.typesafe.play" && x.crossVersion.isInstanceOf[CrossVersion.Binary]) {
        x cross CrossVersion.for3Use2_13
      } else {
        x
      }
    }
  },
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(Publish.nonPublishSettings)
  .settings(
    name := "dbcache"
  )
  .aggregate(core, mysql, postgresql, play, example, examplePlay)

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
    ),
    playScala3workaround,
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
      "org.scalikejdbc" %% "scalikejdbc" % "4.3.1",
      "org.scalikejdbc" %% "scalikejdbc-config" % "4.3.1",
      "org.flywaydb" %% "flyway-play" % "9.1.0" cross CrossVersion.for3Use2_13,
      "org.flywaydb" % "flyway-mysql" % flywayVersion
    ),
    playScala3workaround,
).dependsOn(mysql, postgresql, play)
