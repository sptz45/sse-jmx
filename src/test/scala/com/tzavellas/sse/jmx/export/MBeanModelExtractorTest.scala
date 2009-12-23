package com.tzavellas.sse.jmx.export

import javax.management.modelmbean.ModelMBeanAttributeInfo
import org.junit.Test
import org.junit.Assert._

class MBeanModelExtractorTest {
  
  import MBeanModelExtractorTest._
  
  object extractor extends MBeanModelExtractor {
    def attributes(c: Class[_]) = Array()
    def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]) = Array()
  }

  @Test
  def should_use_the_description_from_annotation() {
    assertEquals("the description", extractor.description(classOf[DescriptionInAnnotation]))
  }
  
  @Test
  def should_use_simple_class_name_if_no_description_in_annotation() {
    assertEquals("EmptyDescriptionInAnnotation", extractor.description(classOf[EmptyDescriptionInAnnotation]))
  }
  
  @Test
  def should_use_simple_class_name_if_no_annotation() {
    assertEquals("NoAnnotation", extractor.description(classOf[NoAnnotation]))
  }
}

object MBeanModelExtractorTest {
  
  @ManagedResource(description="the description")
  class DescriptionInAnnotation
  
  @ManagedResource
  class EmptyDescriptionInAnnotation
  
  class NoAnnotation
}