package com.github.tototoshi.dbcache

import java.io.NotSerializableException

import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

case class CaseClass(a: Int)

class SerializerTest extends FunSuite with PropertyChecks {

  def testSerializeAndDeserialize[T](o: T): Unit = {
    val serializer = new Serializer
    val serialized = serializer.serialize(o)
    assert(serializer.deserialize[T](serialized) === o)
  }

  test("can serialize and deserialize AnyVal") {
    forAll { (s: Short, n: Int, f: Float, d: Double, l: Long, b: Boolean) =>
      testSerializeAndDeserialize[Short](s)
      testSerializeAndDeserialize[Int](n)
      testSerializeAndDeserialize[Float](f)
      testSerializeAndDeserialize[Double](d)
      testSerializeAndDeserialize[Long](l)
      testSerializeAndDeserialize[Boolean](b)
    }
  }

  test("can serialize and deserialize serializable") {
    forAll { (i: Int) =>
      testSerializeAndDeserialize[CaseClass](CaseClass(i))
      testSerializeAndDeserialize[SerializableClass](new SerializableClass(i))
    }
  }

  test("throw exception when passed object is not serializable") {
    forAll { (i: Int) =>
      intercept[NotSerializableException] {
        testSerializeAndDeserialize[NotSerializableClass](new NotSerializableClass(1))
      }
    }
  }

}
