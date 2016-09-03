package modules

import java.sql.Connection
import javax.inject.{ Inject, Named, Provider }

import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.postgresql.PostgreSQLCache
import com.github.tototoshi.dbcache.{ ConnectionFactory, DBCache }
import play.api.Environment
import play.api.cache.CacheApi
import scalikejdbc.ConnectionPool

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

class MySQLCacheApiProvider @Inject()(
  @Named("mysql") connectionPool: ConnectionPool,
  environment: Environment
) extends Provider[CacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = connectionPool.borrow()
  }

  override def get(): CacheApi = {
    val mysqlCache = new MySQLCache(connectionFactory, environment.classLoader)
    new DBCacheApi(mysqlCache)
  }

}

class PostgreSQLCacheApi @Inject()(
  @Named("postgresql") connectionPool: ConnectionPool,
  environment: Environment
) extends Provider[CacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = connectionPool.borrow()
  }

  override def get(): CacheApi = {
    val postgresqlCache = new PostgreSQLCache(connectionFactory, environment.classLoader)
    new DBCacheApi(postgresqlCache)
  }

}

class DBCacheApi(myCache: DBCache) extends CacheApi {

  def set(key: String, value: Any, expiration: Duration): Unit =
    myCache.set(key, value, expiration)

  def get[A](key: String)(implicit ct: ClassTag[A]): Option[A] =
    myCache.get[A](key)

  def getOrElse[A: ClassTag](key: String, expiration: Duration)(orElse: => A) =
    myCache.getOrElse(key, expiration)(orElse)

  def remove(key: String) = myCache.remove(key)

}