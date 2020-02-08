package com.github.tototoshi.dbcache.mysql

import java.sql.DriverManager

import com.github.tototoshi.dbcache.util.Control
import com.github.tototoshi.dbcache.{CacheEntry, ConnectionFactory}
import org.flywaydb.core.Flyway
import org.scalatest.{Outcome, fixture}
import org.scalatest.funsuite
import org.scalatest.matchers.should.Matchers

class MySQLCacheDatabaseTest extends funsuite.FixtureAnyFunSuite with Matchers with Control {

  override type FixtureParam = ConnectionFactory

  val driver   = sys.env.getOrElse("DB_TEST_MYSQL_DRIVER"  , "com.mysql.jdbc.Driver")
  val url      = sys.env.getOrElse("DB_TEST_MYSQL_URL"     , "jdbc:mysql://localhost/dbcache_test")
  val name     = sys.env.getOrElse("DB_TEST_MYSQL_USER"    , "travis")
  val password = sys.env.getOrElse("DB_TEST_MYSQL_PASSWORD", "")

  override protected def withFixture(test: OneArgTest): Outcome = {

    def migrate(): Unit = {
      val flyway = Flyway
        .configure
        .dataSource(url, name, password)
        .locations("migration/mysql")
        .load()

      flyway.clean()
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

    cache.get("key") should be(Symbol("empty"))

    cache.remove("key")

    cache.get("key") should be(Symbol("empty"))
  }

}
