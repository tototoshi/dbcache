package com.github.tototoshi.dbcache.postgresql

import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

class PostgreSQLCache(connectionFactory: ConnectionFactory) extends DBCache {

  val cacheDatabase = new PostgreSQLCacheDatabase(connectionFactory)

}
