package com.github.tototoshi.dbcache.mysql

import com.github.tototoshi.dbcache.{ConnectionFactory, DBCache}

class MySQLCache(connectionFactory: ConnectionFactory, classLoader: ClassLoader = null)
  extends DBCache(new MySQLCacheDatabase(connectionFactory), classLoader)
