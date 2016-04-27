name := """dbcache"""

version := "1.0-SNAPSHOT"

lazy val mysqlDependencies = Seq(
  "mysql" % "mysql-connector-java" % "6.0.2" % "provided"
)

lazy val postgresqlDependencies = Seq(
  "org.postgresql" % "postgresql" % "9.4.1208.jre7"
)

lazy val commonDependencies = Seq(
  "org.flywaydb" % "flyway-core" % "4.0",
  "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "org.mockito" % "mockito-all" % "2.0.2-beta" % "test"
)

lazy val root = (project in file("."))
  .aggregate(core, mysql, postgresql)

lazy val core = (project in file("core"))
  .settings(
    name := "dbcache",
    scalaVersion := "2.11.8",
    libraryDependencies ++= commonDependencies
)

lazy val mysql = (project in file("mysql"))
  .settings(
    name := "dbcache-mysql",
    scalaVersion := "2.11.8",
    libraryDependencies ++= commonDependencies ++ mysqlDependencies
).dependsOn(core)

lazy val postgresql = (project in file("postgresql"))
  .settings(
    name := "dbcache-postgresql",
    scalaVersion := "2.11.8",
    libraryDependencies ++= commonDependencies ++ postgresqlDependencies
).dependsOn(core)

lazy val example = (project in file("example"))
  .settings(
    name := "dbcache-example",
    scalaVersion := "2.11.8",
    libraryDependencies ++= commonDependencies ++ Seq(
      "mysql" % "mysql-connector-java" % "6.0.2"
    )
).dependsOn(mysql)

lazy val examplePlay = (project in file("example-play"))
  .enablePlugins(PlayScala)
  .settings(
    name := "dbcache-example-play",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      "org.scalikejdbc" %% "scalikejdbc" % "2.3.5",
      "org.scalikejdbc" %% "scalikejdbc-config" % "2.3.5",
      "mysql" % "mysql-connector-java" % "6.0.2",
      "org.flywaydb" %% "flyway-play" % "2.3.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    ),
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
).dependsOn(mysql, postgresql)
