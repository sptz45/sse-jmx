/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._
import javax.management.modelmbean.ModelMBeanInfo
import java.io._

class NoGetterAndSetterMBeanInfoTest {
  
  @Test
  def attributes_are_removed_from_operations_after_serialization() {
    val original = AnnotationMBeanInfoAssembler.createMBeanInfo(classOf[ManagedObject])
    val wrapped = new NoGetterAndSetterMBeanInfo(original)
    val serialized = serializeAndReadBack(wrapped)
    
    assertHasOperations(original, "a", "b") // asserts JDK bug 6339571 still exists
    assertHasOperations(wrapped, "a", "b")  // asserts JDK bug 6339571 still exists
    assertHasNoOperations(serialized, "a", "b")
  }
  
  def serializeAndReadBack(ref: AnyRef) = {
    val bytes = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bytes)
    out.writeObject(ref)
    val in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray))
    in.readObject.asInstanceOf[ModelMBeanInfo]
  }
  
  def assertHasOperations(info: ModelMBeanInfo, ops: String*) {
    for (op <- ops)
      assertTrue(info.getOperations.exists(_.getName == op))
  }
  
  def assertHasNoOperations(info: ModelMBeanInfo, ops: String*) {
    for (op <- ops)
      assertFalse(info.getOperations.exists(_.getName == op))
  }

  class ManagedObject {
    @Managed var a: Int = 0
    @Managed val b: Int = 0
  }
}
