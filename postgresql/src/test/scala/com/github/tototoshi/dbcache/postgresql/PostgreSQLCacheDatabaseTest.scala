package com.github.tototoshi.dbcache.postgresql

import java.sql.{Connection, DriverManager}
import java.time.Clock

import com.github.tototoshi.dbcache.{CacheEntry, ConnectionFactory}
import com.github.tototoshi.dbcache.util.Control
import org.flywaydb.core.Flyway
import org.scalatest.{Outcome, fixture}
import org.scalatest.funsuite
import org.scalatest.matchers.should.Matchers

class PostgreSQLCacheDatabaseTest extends funsuite.FixtureAnyFunSuite with Matchers with Control {

  override type FixtureParam = ConnectionFactory

  val driver   = sys.env.getOrElse("DB_TEST_POSTGRES_DRIVER"  , "org.postgresql.Driver")
  val url      = sys.env.getOrElse("DB_TEST_POSTGRES_URL"     , "jdbc:postgresql://localhost:5432/dbcache_test")
  val name     = sys.env.getOrElse("DB_TEST_POSTGRES_USER"    , "postgres")
  val password = sys.env.getOrElse("DB_TEST_POSTGRES_PASSWORD", "password")

  override protected def withFixture(test: OneArgTest): Outcome = {

    def migrate(): Unit = {
      val flyway = Flyway
        .configure
        .dataSource(url, name, password)
        .locations("migration/postgresql")
        .load()
   
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

    cache.get("key") should be(Symbol("empty"))

    cache.remove("key")

    cache.get("key") should be(Symbol("empty"))
  }

}
