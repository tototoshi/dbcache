package com.github.tototoshi.dbcache.play

import com.github.tototoshi.dbcache.DBCache
import com.github.tototoshi.dbcache.util.Control
import play.api.cache.CacheApi

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag


class DBCacheApi(myCache: DBCache) extends CacheApi with Control {

  def set(key: String, value: Any, expiration: Duration): Unit =
    myCache.set(key, value, expiration)

  def get[A](key: String)(implicit ct: ClassTag[A]): Option[A] =
    myCache.get(key)

  def getOrElse[A: ClassTag](key: String, expiration: Duration)(orElse: => A) =
    myCache.getOrElse(key, expiration)(orElse)

  def remove(key: String) = myCache.remove(key)

}
