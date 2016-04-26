package com.github.tototoshi.dbcache.postgresql

import java.sql.{Connection, DriverManager}
import java.time.Clock

import com.github.tototoshi.dbcache.{CacheEntry, ConnectionFactory}
import com.github.tototoshi.dbcache.util.Control
import org.flywaydb.core.Flyway
import org.scalatest.{Matchers, Outcome, fixture}

class PostgreSQLCacheDatabaseTest extends fixture.FunSuite with Matchers with Control {

  override type FixtureParam = ConnectionFactory

  val driver   = sys.env.getOrElse("DB_TEST_POSTGRES_DRIVER"  , sys.error("environment variable DB_TEST_POSTGRES_DRIVER is not set"))
  val url      = sys.env.getOrElse("DB_TEST_POSTGRES_URL"     , sys.error("environment variable DB_TEST_POSTGRES_URL is not set"))
  val name     = sys.env.getOrElse("DB_TEST_POSTGRES_USER"    , sys.error("environment variable DB_TEST_POSTGRES_USER is not set"))
  val password = sys.env.getOrElse("DB_TEST_POSTGRES_PASSWORD", sys.error("environment variable DB_TEST_POSTGRES_PASSWORD is not set"))

  override protected def withFixture(test: OneArgTest): Outcome = {

    def migrate(): Unit = {
      val flyway = new Flyway()
      flyway.setDataSource(url, name, password)
      flyway.setLocations("migration/postgresql")
      flyway.clean()
      flyway.migrate()
    }

    val connectionFactory = new ConnectionFactory {
      override def get(): Connection = {
        DriverManager.getConnection(url, name, password)
      }
    }

    migrate()
    test(connectionFactory)
  }

  test("#set, #get and #delete") { database =>

    def now = System.currentTimeMillis()

    val cache = new PostgreSQLCacheDatabase(database)

    val expiration = 1000

    val entry = CacheEntry("key", "value".getBytes(), Some(now + expiration))

    cache.set(entry)

    cache.get("key").get.value should equal(entry.value)

    Thread.sleep(expiration)

    cache.get("key") should be('empty)

    cache.remove("key")

    cache.get("key") should be('empty)
  }

}