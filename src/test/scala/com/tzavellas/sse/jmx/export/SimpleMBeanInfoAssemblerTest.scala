/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._

class SimpleMBeanInfoAssemblerTest {

  def assembler = SimpleMBeanInfoAssembler

  @Test
  def vars_are_mapped_to_writeable_attributes() {
    val attrs = assembler.attributes(classOf[CacheConfig])
    val size = attrs.find(_.getName == "size").get
    assertTrue(size.isReadable)
    assertTrue(size.isWritable)
    assertEquals("size", size.getDescriptor().getFieldValue("getMethod"))
  }

  @Test
  def methods_with_no_args_returning_non_unit_are_readonly_attributes()  {
    val attrs = assembler.attributes(classOf[CacheConfig])
    val evictionPolicy = attrs.find(_.getName == "evictionPolicy").get
    assertTrue(evictionPolicy.isReadable)
    assertFalse(evictionPolicy.isWritable)
    assertEquals("evictionPolicy", evictionPolicy.getDescriptor.getFieldValue("getMethod"))
  }

  @Test
  def exclude_methods_common_to_all_object_from_mapping_to_attributes()  {
    val attrs = assembler.attributes(classOf[CacheConfig])
    assertFalse(attrs.exists(attr => assembler.excludedAttributes.contains(attr.getName)))
  }

  @Test
  def methods_with_no_args_returning_unit_are_mapped_to_operations() {
    val ops = assembler.operations(classOf[Cache], Array())
    assertFalse(ops.exists(_.getName == "evict"))
    assertTrue(ops.exists(_.getName == "evictAll"))
    assertEquals("evictAll", ops.find(_.getName == "evictAll").get.getDescription)
  }

  @Test
  def exclude_methods_common_to_all_object_from_mapping_to_operations() {
    val ops = assembler.operations(classOf[Cache], Array())
    assertFalse(ops.exists(op => assembler.excludedOperations.contains(op.getName)))
  }


  class CacheConfig(var size: Int, val evictionPolicy: String)

  class Cache(val config: CacheConfig) {
    def evict(id: String) { }
    def evictAll() { }
  }
}

