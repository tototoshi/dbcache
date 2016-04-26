package com.github.tototoshi.dbcache.mysql

import java.sql.DriverManager

import com.github.tototoshi.dbcache.util.Control
import com.github.tototoshi.dbcache.{CacheEntry, ConnectionFactory}
import org.flywaydb.core.Flyway
import org.scalatest.{Matchers, Outcome, fixture}

class MySQLCacheDatabaseTest extends fixture.FunSuite with Matchers with Control {

  override type FixtureParam = ConnectionFactory

  val driver   = sys.env.getOrElse("DB_TEST_MYSQL_DRIVER"  , sys.error("environment variable DB_TEST_MYSQL_DRIVER is not set"))
  val url      = sys.env.getOrElse("DB_TEST_MYSQL_URL"     , sys.error("environment variable DB_TEST_MYSQL_URL is not set"))
  val name     = sys.env.getOrElse("DB_TEST_MYSQL_USER"    , sys.error("environment variable DB_TEST_MYSQL_USER is not set"))
  val password = sys.env.getOrElse("DB_TEST_MYSQL_PASSWORD", sys.error("environment variable DB_TEST_MYSQL_PASSWORD is not set"))

  override protected def withFixture(test: OneArgTest): Outcome = {

    def migrate(): Unit = {
      val flyway = new Flyway()
      flyway.setDataSource(url, name, password)
      flyway.setLocations("migration/mysql")
      flyway.migrate()
    }

    val connectionFactory = new ConnectionFactory {
      def get() = DriverManager.getConnection(url, name, password)
    }

    migrate()
    test(connectionFactory)

  }

  test("#set, #get and #delete") { connection =>

    val cache = new MySQLCacheDatabase(connection)

    val expiration = 1000

    val timestamp = System.currentTimeMillis()

    val entry = CacheEntry("key", "value".getBytes(), Some(timestamp + expiration))

    cache.set(entry)

    cache.get("key").get.value should equal(entry.value)


    Thread.sleep(expiration + 1000 /* mysql datetime doesn't have millisecond information */)

    cache.get("key") should be('empty)

    cache.remove("key")

    cache.get("key") should be('empty)
  }

}
