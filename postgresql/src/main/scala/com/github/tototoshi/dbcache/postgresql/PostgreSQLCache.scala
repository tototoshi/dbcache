package com.github.tototoshi.dbcache.postgresql

import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

class PostgreSQLCache(connectionFactory: ConnectionFactory, classLoader: ClassLoader = null)
  extends DBCache(new PostgreSQLCacheDatabase(connectionFactory), classLoader)