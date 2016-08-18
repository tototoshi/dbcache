package com.github.tototoshi.dbcache.mysql

import java.sql.{ResultSet, Timestamp}

import com.github.tototoshi.dbcache.util.Control
import com.github.tototoshi.dbcache.{CacheDatabase, CacheEntry, ConnectionFactory}

class MySQLCacheDatabase(connectionFactory: ConnectionFactory) extends CacheDatabase with Control {

  def now = System.currentTimeMillis()

  def set(entry: CacheEntry): Unit = using(connectionFactory.get()) { connection =>
    val expiredAt = entry.expiration.map(new Timestamp(_)).orNull
    val timestamp = new Timestamp(now)
    val createdAt = timestamp
    val updatedAt = timestamp
    val stmt = connection.prepareStatement(
      """
        |INSERT INTO
        |  `cache_entries`(`cache_key`, `cache_value`, `expired_at`, `created_at`, `updated_at`)
        |VALUES
        |  (?, ?, ?, ?, ?)
        |ON DUPLICATE KEY UPDATE
        |  `cache_value` = ?,
        |  `expired_at` = ?,
        |  `updated_at` = ?
        |
      """.stripMargin)
    stmt.setString(1, entry.key)
    stmt.setBytes(2, entry.value)
    stmt.setTimestamp(3, expiredAt)
    stmt.setTimestamp(4, createdAt)
    stmt.setTimestamp(5, updatedAt)
    stmt.setBytes(6, entry.value)
    stmt.setTimestamp(7, expiredAt)
    stmt.setTimestamp(8, updatedAt)
    stmt.executeUpdate()
  }

  def get(key: String): Option[CacheEntry] = {
    using(connectionFactory.get()) { connection =>
      val stmt = connection.prepareStatement(
        """
          |SELECT
          |  `cache_key`,
          |  `cache_value`,
          |  `expired_at`
          |FROM
          |  `cache_entries`
          |WHERE
          |  `cache_key` = ?
          |  AND (`expired_at` IS NULL OR `expired_at` > ?)
        """.stripMargin)
      stmt.setString(1, key)
      stmt.setTimestamp(2, new Timestamp(now))
      using(stmt.executeQuery()) { rs =>
        var result: Option[CacheEntry] = None
        while(rs.next()) {
          result = Some(mapping(rs))
        }
        result
      }
    }
  }

  def remove(key: String): Unit = {
    using(connectionFactory.get()) { connection =>
      val stmt = connection.prepareStatement("DELETE FROM `cache_entries` WHERE `cache_key` = ?")
      stmt.setString(1, key)
      stmt.executeUpdate()
    }
  }

  private def mapping(rs: ResultSet): CacheEntry =
    CacheEntry(
      rs.getString("cache_key"),
      rs.getBytes("cache_value"),
      Option(rs.getTimestamp("expired_at")).map(_.getTime)
    )
}
