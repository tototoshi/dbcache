package modules

import java.sql.Connection

import com.github.tototoshi.dbcache.ConnectionFactory
import com.github.tototoshi.dbcache.mysql.MySQLCache
import com.github.tototoshi.dbcache.play.DBCacheApi
import com.github.tototoshi.dbcache.postgresql.PostgreSQLCache
import javax.inject.{Inject, Named, Provider}
import play.api.Environment
import play.api.cache.SyncCacheApi
import scalikejdbc.ConnectionPool

class MySQLCacheApiProvider @Inject()(
  @Named("mysql") connectionPool: ConnectionPool,
  environment: Environment
) extends Provider[SyncCacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = {
      val connection = connectionPool.borrow()
      connection.setReadOnly(false)
      connection
    }
  }

  override def get(): SyncCacheApi = {
    val mysqlCache = new MySQLCache(connectionFactory, environment.classLoader)
    new DBCacheApi(mysqlCache)
  }

}

class PostgreSQLCacheApi @Inject()(
  @Named("postgresql") connectionPool: ConnectionPool,
  environment: Environment
) extends Provider[SyncCacheApi] {

  val connectionFactory = new ConnectionFactory {
    override def get(): Connection = {
      val connection = connectionPool.borrow()
      connection.setReadOnly(false)
      connection
    }
  }

  override def get(): SyncCacheApi = {
    val postgresqlCache = new PostgreSQLCache(connectionFactory, environment.classLoader)
    new DBCacheApi(postgresqlCache)
  }

}


