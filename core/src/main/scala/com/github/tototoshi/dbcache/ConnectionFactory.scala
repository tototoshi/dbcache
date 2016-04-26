package com.github.tototoshi.dbcache

import java.sql.Connection

trait ConnectionFactory {

  def get(): Connection

}
