# dbcache

RDB as cache storage

## Setup

dbcache is a cache library which uses RDB (mysql and postgresql) as backend. it uses a table named `cache_entries` which has columns named `key`, `value`, `expired_at` and `created_at` like below.


```sql
CREATE TABLE `cache_entries` (
  `key` varchar(191) PRIMARY KEY,
  `value` mediumblob NOT NULL,
  `expired_at` datetime,
  `created_at` datetime NOT NULL,
  INDEX (`expired_at`),
  INDEX (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
```

You can add more constraint and index (for example: expired_at as NOT NULL).


```scala
package example

import java.sql.{Connection, DriverManager}
import java.util.Date

import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

import scala.concurrent.duration.Duration

case class User(id: Int, name: String)

object Example {

  class ExampleConnectionFactory extends ConnectionFactory {

    val driver   = "com.mysql.cj.jdbc.Driver"
    val url      = "jdbc:mysql//localhost/mydb"
    val user     = "user"
    val password = "password"

    Class.forName(driver)

    override def get(): Connection = {
      DriverManager.getConnection(url, user, password)
    }
  }

  def main(args: Array[String]): Unit =  {

    val cache: DBCache = new MySQLCache(new ExampleConnectionFactory)

    cache.set("key", User(1, "John"), Duration("5s"))

    1.to(10).foreach { _ =>
      val user = cache.get[User]("key")
      val date = new Date()
      println(s"$date: $user")
      Thread.sleep(1000)
    }

  }

}


/* output
Tue Apr 26 22:26:48 JST 2016: Some(User(1,John))
Tue Apr 26 22:26:49 JST 2016: Some(User(1,John))
Tue Apr 26 22:26:50 JST 2016: Some(User(1,John))
Tue Apr 26 22:26:51 JST 2016: Some(User(1,John))
Tue Apr 26 22:26:52 JST 2016: Some(User(1,John))
Tue Apr 26 22:26:53 JST 2016: None
Tue Apr 26 22:26:54 JST 2016: None
Tue Apr 26 22:26:55 JST 2016: None
Tue Apr 26 22:26:56 JST 2016: None
Tue Apr 26 22:26:57 JST 2016: None
*/
```


## Play framework Support


```scala
// with scalikejdbc
class MySQLCacheApiProvider @Inject() (connectionPool: ConnectionPool) extends Provider[CacheApi] {
  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = connectionPool.borrow()
  }
  override def get(): CacheApi = {
    val mysqlCache = new MySQLCache(connectionFactory)
    new DBCacheApi(mysqlCache)
  }
}


class ApplicationModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[CacheApi].toProvider[MySQLCacheApiProvider].eagerly(),
  )
}

```
