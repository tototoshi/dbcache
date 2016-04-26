package com.github.tototoshi.dbcache.postgresql

import java.sql.{ResultSet, Timestamp}

import com.github.tototoshi.dbcache.util.Control
import com.github.tototoshi.dbcache.{CacheDatabase, CacheEntry, ConnectionFactory}


class PostgreSQLCacheDatabase (db: ConnectionFactory) extends CacheDatabase with Control {

  def now = System.currentTimeMillis()

  def set(entry: CacheEntry): Unit = using(db.get()) { connection =>
    val expiredAt = entry.expiration.map(new Timestamp(_)).orNull
    val createdAt = new Timestamp(now)
    val stmt = connection.prepareStatement(
      """
        |INSERT INTO
        |  cache_entries(key, value, expired_at, created_at)
        |VALUES
        |  (?, ?, ?, ?)
        |ON CONFLICT (key) DO UPDATE
        |SET
        |  value = ?,
        |  expired_at = ?,
        |  created_at = ?
        |
      """.stripMargin)
    stmt.setString(1, entry.key)
    stmt.setBytes(2, entry.value)
    stmt.setTimestamp(3, expiredAt)
    stmt.setTimestamp(4, createdAt)
    stmt.setBytes(5, entry.value)
    stmt.setTimestamp(6, expiredAt)
    stmt.setTimestamp(7, createdAt)
    stmt.executeUpdate()
  }

  def get(key: String): Option[CacheEntry] = {
    using(db.get()) { connection =>
      val stmt = connection.prepareStatement(
        """
          |SELECT
          |  *
          |FROM
          |  cache_entries
          |WHERE
          |  key = ?
          |  AND (expired_at IS NULL OR expired_at > ?)
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
    using(db.get()) { connection =>
      val stmt = connection.prepareStatement("DELETE FROM cache_entries WHERE key = ?")
      stmt.setString(1, key)
      stmt.executeUpdate()
    }
  }

  private def mapping(rs: ResultSet): CacheEntry =
    CacheEntry(
      rs.getString("key"),
      rs.getBytes("value"),
      Option(rs.getTimestamp("expired_at")).map(_.getTime)
    )
}