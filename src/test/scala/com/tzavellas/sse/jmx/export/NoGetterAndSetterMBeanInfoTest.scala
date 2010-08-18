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
  
  def assembler = AnnotationMBeanInfoAssembler
  
  @Test
  def attributes_are_removed_from_operations_after_serialization() {
    val info = new NoGetterAndSetterMBeanInfo(assembler.createMBeanInfo(classOf[ManagedObject]))
    assertTrue(info.getOperations.exists(_.getName == "a"))
    val serialized = serializeAndReadBack(info)
    assertFalse(serialized.getOperations.exists(_.getName == "a"))
    assertFalse(serialized.getOperations.exists(_.getName == "b"))
  }
  
  
  def serializeAndReadBack(ref: AnyRef) = {
    val bytes = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bytes)
    out.writeObject(ref)
    val in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray))
    in.readObject.asInstanceOf[ModelMBeanInfo]
  }

  class ManagedObject {
    @Managed var a: Int = 0
    @Managed val b: Int = 0
  }
}
