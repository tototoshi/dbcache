name := """dbcache"""

lazy val mysqlDependency = "mysql" % "mysql-connector-java" % "6.0.2"
lazy val postgresqlDependency = "org.postgresql" % "postgresql" % "9.4.1208.jre7"

lazy val testDependencies = Seq(
  "org.flywaydb" % "flyway-core" % "4.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "org.mockito" % "mockito-all" % "2.0.2-beta" % "test"
)

lazy val root = (project in file("."))
  .settings(
    name := "dbcache",
    organization := "com.github.tototoshi"
  )
  .settings(Publish.nonPublishSettings)
  .aggregate(core, mysql, postgresql)

lazy val core = (project in file("core"))
  .settings(
    name := "dbcache-core",
    organization := "com.github.tototoshi",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= testDependencies
).settings(Publish.settings)

lazy val mysql = (project in file("mysql"))
  .settings(
    name := "dbcache-mysql",
    organization := "com.github.tototoshi",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= testDependencies ++ Seq(
      mysqlDependency % "provided"
    )
).settings(Publish.settings).dependsOn(core)

lazy val postgresql = (project in file("postgresql"))
  .settings(
    name := "dbcache-postgresql",
    organization := "com.github.tototoshi",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= testDependencies ++ Seq(
      postgresqlDependency % "provided"
    )
).settings(Publish.settings).dependsOn(core)

lazy val example = (project in file("example"))
  .settings(
    name := "dbcache-example",
    scalaVersion := "2.11.8",
    libraryDependencies ++= testDependencies ++ Seq(
      mysqlDependency,
      postgresqlDependency
    )
).dependsOn(mysql, postgresql)

lazy val examplePlay = (project in file("example-play"))
  .enablePlugins(PlayScala)
  .settings(
    name := "dbcache-example-play",
    scalaVersion := "2.11.8",
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
