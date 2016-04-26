package com.github.tototoshi.dbcache

trait CacheDatabase {

  def set(entry: CacheEntry): Unit

  def get(key: String): Option[CacheEntry]

  def remove(key: String): Unit

}
