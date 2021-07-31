package controllers

import java.util.concurrent.TimeUnit

import javax.inject._
import play.api.cache.SyncCacheApi
import play.api.mvc._

import scala.concurrent.duration.Duration

@Singleton
class HomeController @Inject() (
    cc: ControllerComponents,
    @Named("mysql") mysqlCacheApi: SyncCacheApi,
    @Named("postgresql") postgresqlCacheApi: SyncCacheApi
) extends AbstractController(cc) {

  def index = Action {
    Ok
  }

  def set(key: String, value: String, expiration: Long) = Action {
    mysqlCacheApi.set(key, value, Duration(expiration, TimeUnit.SECONDS))
    postgresqlCacheApi.set(key, value, Duration(expiration, TimeUnit.SECONDS))
    Ok
  }

  def get(key: String) = Action {
    val value1 = mysqlCacheApi.get[Any](key)
    val value2 = postgresqlCacheApi.get[Any](key)
    Ok((value1, value2).toString)
  }

  def remove(key: String) = Action {
    mysqlCacheApi.remove(key)
    postgresqlCacheApi.remove(key)
    Ok
  }

}
