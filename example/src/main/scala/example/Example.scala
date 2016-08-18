package example

import java.sql.{Connection, DriverManager}
import java.util.Date

import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.postgresql.PostgreSQLCache
import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

import scala.concurrent.duration.Duration

case class User(id: Int, name: String)

object Example {

  class ExampleMySQLConnectionFactory extends ConnectionFactory {

    val driver   = sys.env.getOrElse("DB_TEST_MYSQL_DRIVER"  , sys.error("environment variable DB_TEST_MYSQL_DRIVER is not set"))
    val url      = sys.env.getOrElse("DB_TEST_MYSQL_URL"     , sys.error("environment variable DB_TEST_MYSQL_URL is not set"))
    val user     = sys.env.getOrElse("DB_TEST_MYSQL_USER"    , sys.error("environment variable DB_TEST_MYSQL_USER is not set"))
    val password = sys.env.getOrElse("DB_TEST_MYSQL_PASSWORD", sys.error("environment variable DB_TEST_MYSQL_PASSWORD is not set"))

    Class.forName(driver)

    override def get(): Connection = {
      DriverManager.getConnection(url, user, password)
    }
  }

  class ExamplePostgreSQLConnectionFactory extends ConnectionFactory {

    val driver   = sys.env.getOrElse("DB_TEST_POSTGRES_DRIVER"  , sys.error("environment variable DB_TEST_POSTGRES_DRIVER is not set"))
    val url      = sys.env.getOrElse("DB_TEST_POSTGRES_URL"     , sys.error("environment variable DB_TEST_POSTGRES_URL is not set"))
    val user     = sys.env.getOrElse("DB_TEST_POSTGRES_USER"    , sys.error("environment variable DB_TEST_POSTGRES_USER is not set"))
    val password = sys.env.getOrElse("DB_TEST_POSTGRES_PASSWORD", sys.error("environment variable DB_TEST_POSTGRES_PASSWORD is not set"))

    Class.forName(driver)

    override def get(): Connection = {
      DriverManager.getConnection(url, user, password)
    }
  }

  def test(cache: DBCache): Unit = {
    cache.set("key", User(1, "John"), Duration("5s"))

    1.to(10).foreach { _ =>
      val user = cache.get[User]("key")
      val date = new Date()
      println(s"$date: $user")
      Thread.sleep(1000)
    }
  }

  def main(args: Array[String]): Unit =  {
    test(new MySQLCache(new ExampleMySQLConnectionFactory))
    test(new PostgreSQLCache(new ExamplePostgreSQLConnectionFactory))
  }

}
