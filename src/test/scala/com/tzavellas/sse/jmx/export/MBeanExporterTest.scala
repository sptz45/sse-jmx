package com.tzavellas.sse.jmx.export

import javax.management.ObjectName
import org.junit.{Test, After}
import org.junit.Assert._

class MBeanExporterTest {
  
  import MBeanExporterTest._
  
  val exporter = new MBeanExporter
  val server = exporter.server

  @Test
  def register_an_object_as_model_mbean() {
    val conf = new Configuration
    exporter.export(conf)
    assertEquals(conf.reload, server.invoke(exporter.objectName(conf.getClass), "reload", Array(), Array()))
    assertEquals(conf.size, server.invoke(exporter.objectName(conf.getClass), "size", Array(), Array()))
    assertEquals(conf.size, server.getAttribute(exporter.objectName(conf.getClass), "size"))
    server.unregisterMBean(exporter.objectName(classOf[Configuration]))
  }
  
  @Test
  def register_a_standard_mbean() {
    val std = new Standard
    exporter.export(std)
    assertEquals(std.operation, server.invoke(exporter.objectName(std.getClass), "operation", Array(), Array()))
    server.unregisterMBean(exporter.objectName(classOf[Standard]))
  }
  
  @Test
  def register_a_mx_mbean() {
    val s = new Simple
    exporter.export(s)
    assertEquals(s.mxOperation, server.invoke(exporter.objectName(s.getClass), "mxOperation", Array(), Array()))
    server.unregisterMBean(exporter.objectName(classOf[Simple]))
  }
  
  @Test
  def should_use_the_object_name_in_annotation() {
    assertObjectName("com.tzavellas:type=Test", exporter.objectName(classOf[ObjectNameViaAnnotation]))
  }
  
  @Test
  def should_use_default_strategy_if_annotation_has_empty_value() {
    assertObjectName("com.tzavellas.sse.jmx.export:type=EmptyAnnotation", exporter.objectName(classOf[EmptyAnnotation]))
  }
  
  @Test
  def should_use_default_strategy_if_no_annotation_present() {
    assertObjectName("com.tzavellas.sse.jmx.export:type=Configuration", exporter.objectName(classOf[Configuration]))
  }
  
  def assertObjectName(expected: String, actual: ObjectName) {
    assertEquals(expected, actual.toString)
  }
}

object MBeanExporterTest {
  
  class Configuration {
    @Managed var size: Int = 10
    @Managed def reload = true
  }

  @ManagedResource(objectName="com.tzavellas:type=Test")
  class ObjectNameViaAnnotation

  @ManagedResource
  class EmptyAnnotation
  
  trait StandardMBean { def operation: String }
  class Standard extends StandardMBean { def operation = "success" }
  
  trait SimpleMXBean { def mxOperation: String }
  class Simple extends SimpleMXBean { def mxOperation = "success" }
}