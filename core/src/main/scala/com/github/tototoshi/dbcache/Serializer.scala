package com.github.tototoshi.dbcache

import java.io._

import com.github.tototoshi.dbcache.util.Control

class Serializer(classLoader: ClassLoader = null) extends Control {

  def deserialize[T](data: Array[Byte]): T =
    using (new ByteArrayInputStream(data)) { buf =>
      using (createObjectInputStream(buf)) { in =>
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

  private def createObjectInputStream(inputStream: InputStream): ObjectInputStream = {
    Option(classLoader)
      .map { cl => new ObjectInputStreamWithCustomClassLoader(inputStream, cl) }
      .getOrElse(new ObjectInputStream(inputStream))
  }

}

class ObjectInputStreamWithCustomClassLoader(
  stream: InputStream,
  customClassloader: ClassLoader
) extends ObjectInputStream(stream) {
  override protected def resolveClass(desc: ObjectStreamClass) = {
    Class.forName(desc.getName, false, customClassloader)
  }
}