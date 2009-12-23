package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._

class SimpleMBeanInfoAssemblerTest {

  import SimpleMBeanInfoAssemblerTest._
  
  @Test
  def attributes_finder_test()  {
    val attrs = SimpleMBeanInfoAssembler.attributes(classOf[CacheConfig])

    val size = attrs.find(_.getName == "size").get
    assertTrue(size.isReadable)
    assertTrue(size.isWritable)
    assertEquals("size", size.getDescriptor().getFieldValue("getMethod"))

    val evictionPolicy = attrs.find(_.getName == "evictionPolicy").get
    assertTrue(evictionPolicy.isReadable)
    assertFalse(evictionPolicy.isWritable)
  }

  @Test
  def attributes_excluded_method_names()  {
    val attrs = SimpleMBeanInfoAssembler.attributes(classOf[CacheConfig])

    val size = attrs.find(_.getName == "size").get
    assertFalse(attrs.exists(x => SimpleMBeanInfoAssembler.excludedAttributes.contains(x.getName)))
  }

  @Test
  def operations_test() {
    val ops = SimpleMBeanInfoAssembler.operations(classOf[Cache], Array())
    
    val evict = ops.find(_.getName == "evictAll").get
    assertEquals("evictAll", evict.getDescription)
    assertEquals(None, ops.find(_.getName == "equals"))
  }
}

object SimpleMBeanInfoAssemblerTest {
  
  class CacheConfig(var size: Int, val evictionPolicy: String)

  class Cache(val config: CacheConfig) {
    def evict(id: String) { }
    def evictAll() { }
  }
}
