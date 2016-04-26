package com.github.tototoshi.dbcache

import java.io._

import com.github.tototoshi.dbcache.util.Control

class Serializer extends Control {

  def deserialize[T](data: Array[Byte]): T =
    using (new ByteArrayInputStream(data)) { buf =>
      using (new ObjectInputStream(buf)) { in =>
        in.readObject().asInstanceOf[T]
      }
    }

  def serialize(obj: Any): Array[Byte] = {
    using(new ByteArrayOutputStream()) { baos =>
      using(new ObjectOutputStream(baos)) { oos =>
        oos.writeObject(obj)
        oos.flush()
        baos.toByteArray
      }
    }
  }

}
