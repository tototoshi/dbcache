package com.github.tototoshi.dbcache.util

import scala.language.reflectiveCalls
import scala.util.control.NonFatal

private[dbcache] trait Control {

  def using[Resource <: { def close(): Unit }, Result](resource: Resource)(f: Resource => Result): Result =
    try {
      f(resource)
    } finally {
      try {
        resource.close()
      } catch {
        case NonFatal(_) =>
      }
    }


}
