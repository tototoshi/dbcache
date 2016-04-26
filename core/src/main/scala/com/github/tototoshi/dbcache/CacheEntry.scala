package com.github.tototoshi.dbcache

import scala.concurrent.duration.{Duration, FiniteDuration}

case class CacheEntry(key: String, value: Array[Byte], expiration: Option[Long])

object CacheEntry {

  def apply(key: String, value: Array[Byte], expiration: Duration, timestamp: Long): CacheEntry = {
    CacheEntry(key, value, asLong(expiration, timestamp: Long))
  }

  private def asLong(expiration: Duration, timestamp: Long): Option[Long] = {
    expiration match {
      case infinite: Duration.Infinite => None
      case finite: FiniteDuration => Some(timestamp + (finite.toSeconds * 1000) )
    }
  }
}
