package modules

import akka.Done
import com.github.tototoshi.dbcache.DBCache
import play.api.cache.{AsyncCacheApi, SyncCacheApi}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

class DBCacheApi(db: DBCache) extends SyncCacheApi {

  def set(key: String, value: Any, expiration: Duration): Unit =
    db.set(key, value, expiration)

  def get[A](key: String)(implicit ct: ClassTag[A]): Option[A] =
    db.get[A](key)

  def getOrElseUpdate[A: ClassTag](key: String, expiration: Duration)(orElse: => A): A =
    db.getOrElse(key, expiration)(orElse)

  def remove(key: String): Unit = db.remove(key)

}


class AsyncDBCacheApi(db: DBCache)(implicit context: ExecutionContext) extends AsyncCacheApi {

  override lazy val sync: SyncCacheApi = new DBCacheApi(db)

  override def set(key: String, value: Any, expiration: Duration): Future[Done] = Future {
    sync.set(key, value, expiration)
    Done
  }

  override def remove(key: String): Future[Done] = Future {
    sync.remove(key)
    Done
  }

  override def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: =>Future[A])(implicit evidence: ClassTag[A]): Future[A] = {
    for {
      data <- get[A](key)
      result <- data match {
        case Some(value) => Future.successful(value)
        case None => orElse.map { value =>
          set(key, value, expiration)
          value
        }
      }
    } yield result
  }

  override def get[T](key: String)(implicit evidence: ClassTag[T]): Future[Option[T]] = Future {
    sync.get[T](key)
  }

  override def removeAll(): Future[Done] = {
    throw new UnsupportedOperationException("removeAll is not supported")
  }

}