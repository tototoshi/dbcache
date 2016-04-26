package controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import play.api.cache.CacheApi
import play.api.mvc._

import scala.concurrent.duration.Duration

@Singleton
class HomeController @Inject() (
    @Named("mysql") mysqlCacheApi: CacheApi,
    @Named("postgresql") postgresqlCacheApi: CacheApi
) extends Controller {

  def index = Action {
    Ok
  }

  def set(key: String, value: String, expiration: Long) = Action {
    mysqlCacheApi.set(key, value, Duration(expiration, TimeUnit.SECONDS))
    postgresqlCacheApi.set(key, value, Duration(expiration, TimeUnit.SECONDS))
    Ok
  }

  def get(key: String) = Action {
    val value1 = mysqlCacheApi.get(key)
    val value2 = postgresqlCacheApi.get(key)
    Ok((value1, value2).toString)
  }

  def remove(key: String) = Action {
    mysqlCacheApi.remove(key)
    postgresqlCacheApi.remove(key)
    Ok
  }

}
