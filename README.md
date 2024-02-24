# dbcache

![Scala CI](https://github.com/tototoshi/dbcache/workflows/Scala%20CI/badge.svg)

RDB as a cache storage

## Install

```scala
libraryDependencies += "com.github.tototoshi" %% "dbcache-mysql" % "1.0.0"

// or
// libraryDependencies += "com.github.tototoshi" %% "dbcache-postgresql" % "1.0.0"
```

## Setup

dbcache is a cache library which uses RDB (mysql and postgresql) as backend. it uses a table named `cache_entries` which has columns named `key`, `value`, `expired_at` and `created_at` like below.


```sql
-- mysql
CREATE TABLE `cache_entries` (
  `cache_key` varchar(191) PRIMARY KEY,
  `cache_value` mediumblob NOT NULL,
  `expired_at` datetime,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  INDEX (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

-- postgresql
CREATE TABLE cache_entries (
  cache_key varchar(191) PRIMARY KEY,
  cache_value bytea NOT NULL,
  expired_at timestamp,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
);

create index on cache_entries(expired_at);
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


## With Play framework

|dbcache-play version|Play version|Scala version|
|-------------------:|-----------:|------------:|
|1.0.0               |3.0.x       |2.13/3.3     |
|0.5.0               |2.9.x       |2.13/3.3     |
|0.4.0               |2.8.x       |2.12/2.13    |


Add dbcache-play to dependencies.

```scala
libraryDependencies += "com.github.tototoshi" %% "dbcache-play" % "1.0.0"
```

The code below is Play integration example with scalikejdbc

```scala
import java.sql.Connection

import com.github.tototoshi.dbcache.ConnectionFactory
import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.play.DBCacheApi
import javax.inject.{Inject, Provider}
import play.api.Environment
import play.api.cache.SyncCacheApi
import scalikejdbc.DB

class MySQLCacheApiProvider @Inject()(environment: Environment) extends Provider[SyncCacheApi] {

  private val connectionFactory = new ConnectionFactory {
    override def get(): Connection = {
      DB.autoCommitSession().connection
    }
  }
  override def get(): SyncCacheApi = {
    val mysqlCache = new MySQLCache(connectionFactory, environment.classLoader)
    new DBCacheApi(mysqlCache)
  }
}
```

Disable EhCache which is the default cache backend of Play.

```
play.modules.disabled += "play.api.cache.ehcache.EhCacheModule"
```

