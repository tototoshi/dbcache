package com.github.tototoshi.dbcache.mysql

import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

class MySQLCache(connectionFactory: ConnectionFactory) extends DBCache {

  val cacheDatabase = new MySQLCacheDatabase(connectionFactory)

}
