package modules

import com.github.tototoshi.dbcache.DBCache
import com.github.tototoshi.dbcache.play.DBCacheApi
import play.api.cache.CacheApi
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import scalikejdbc.ConnectionPool


class ApplicationModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    // scalikejdbc
    bind[ConnectionPool].qualifiedWith("mysql").toProvider[MySQLConnectionPoolProvider].eagerly(),
    bind[ConnectionPool].qualifiedWith("postgresql").toProvider[PostgreSQLConnectionPoolProvider].eagerly(),
    bind[ConnectionPoolShutdown].toSelf.eagerly(),

    // DBCache
    bind[CacheApi].qualifiedWith("mysql").toProvider[MySQLCacheApiProvider].eagerly(),
    bind[CacheApi].qualifiedWith("postgresql").toProvider[PostgreSQLCacheApi].eagerly()
  )

}


