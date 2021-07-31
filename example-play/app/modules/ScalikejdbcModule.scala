package modules

import javax.inject.{Inject, Named, Provider, Singleton}

import play.api.Logger
import play.api.inject.ApplicationLifecycle
import scalikejdbc.config.TypesafeConfigReader
import scalikejdbc.{Commons2ConnectionPoolFactory, ConnectionPool}

import scala.concurrent.{ExecutionContext, Future}


class MySQLConnectionPoolProvider extends Provider[ConnectionPool] {
  private val logger = Logger(classOf[MySQLConnectionPoolProvider])
  override def get(): ConnectionPool = {
    val jdbc = TypesafeConfigReader.readJDBCSettings("mysql")
    logger.info("Preparing connection pool...")
    Commons2ConnectionPoolFactory(jdbc.url, jdbc.user, jdbc.password)
  }
}

class PostgreSQLConnectionPoolProvider extends Provider[ConnectionPool] {
  private val logger = Logger(classOf[PostgreSQLConnectionPoolProvider])
  override def get(): ConnectionPool = {
    val jdbc = TypesafeConfigReader.readJDBCSettings("postgresql")
    logger.info("Preparing connection pool...")
    Commons2ConnectionPoolFactory(jdbc.url, jdbc.user, jdbc.password)
  }
}

@Singleton
class ConnectionPoolShutdown @Inject() (
    lifecycle: ApplicationLifecycle,
    @Named("mysql") mysqlConnectionPool: ConnectionPool,
    @Named("postgresql") postgresqlConnectionPool: ConnectionPool,
    executionContext: ExecutionContext) {
  private val logger = Logger(classOf[ConnectionPoolShutdown])
  lifecycle.addStopHook(() => Future {
    logger.info("Shutdown connection pool")
    mysqlConnectionPool.close()
    postgresqlConnectionPool.close()
  }(executionContext))
}
