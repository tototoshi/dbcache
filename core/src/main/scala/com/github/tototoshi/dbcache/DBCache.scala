package com.github.tototoshi.dbcache

import scala.concurrent.duration.Duration

trait DBCache {

  protected val cacheDatabase: CacheDatabase

  protected val serializer = new Serializer

  def now = System.currentTimeMillis()

  def set(key: String, value: Any, expiration: Duration): Unit = {
    val entry = CacheEntry(key, serializer.serialize(value), expiration, now)
    cacheDatabase.set(entry)
  }

  def get[A](key: String): Option[A] =
    cacheDatabase
        .get(key)
        .map { entry => serializer.deserialize[A](entry.value) }

  def getOrElse[A](key: String, expiration: Duration)(orElse: => A) = {
    get[A](key).getOrElse {
      val value = orElse
      set(key, value, expiration)
      value
    }
  }

  def remove(key: String) = cacheDatabase.remove(key)

}
