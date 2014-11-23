package com.tzavellas.sse.jmx.export

import org.junit.Test
import javax.management.modelmbean.ModelMBeanAttributeInfo


class MBeanInfoAssemblerTest {

  @Test(expected=classOf[RuntimeException])
  def throw_exception_when_no_extractor_for_class(): Unit = {
    val assembler = new MBeanInfoAssembler(dummyExtractor)
    assembler.createMBeanInfo(this.getClass)
  }

  private val dummyExtractor = new MBeanModelExtractor {
    def canExtractModel(c: Class[_]) = false
    def attributes(c: Class[_]) = ???
    def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]) = ???
  }
}
