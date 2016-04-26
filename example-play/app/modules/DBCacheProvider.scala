package modules

import java.sql.Connection
import javax.inject.{Inject, Named, Provider}

import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.play.DBCacheApi
import com.github.tototoshi.dbcache.postgresql.PostgreSQLCache
import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}
import play.api.cache.CacheApi
import scalikejdbc.ConnectionPool

class MySQLCacheApiProvider @Inject() (@Named("mysql") connectionPool: ConnectionPool) extends Provider[CacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = connectionPool.borrow()
  }

  override def get(): CacheApi = {
    val mysqlCache = new MySQLCache(connectionFactory)
    new DBCacheApi(mysqlCache)
  }

}

class PostgreSQLCacheApi @Inject() (@Named("postgresql") connectionPool: ConnectionPool) extends Provider[CacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = connectionPool.borrow()
  }

  override def get(): CacheApi = {
    val postgresqlCache = new PostgreSQLCache(connectionFactory)
    new DBCacheApi(postgresqlCache)
  }

}
