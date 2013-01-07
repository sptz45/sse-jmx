/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.ObjectName
import org.junit.{Test, After}
import org.junit.Assert._
import com.tzavellas.sse.jmx.IfAlreadyExists
import javax.management.InstanceNotFoundException

class MBeanExporterTest {
  
  val exporter = new MBeanExporter
  val server   = exporter.server

  @Test
  def register_an_object_as_model_mbean() {
    val conf = new Configuration
    exporter.export(conf)
    assertEquals(conf.reload, invoke[Configuration]("reload"))
    assertEquals(conf.size,   invoke[Configuration]("size"))
    assertEquals(conf.size,   attribute[Configuration]("size"))
    server.unregisterMBean(objectName[Configuration])
  }
  
  @Test
  def register_a_standard_mbean() {
    val std = new Standard
    exporter.export(std)
    assertEquals(std.operation, invoke[Standard]("operation"))
    server.unregisterMBean(objectName[Standard])
  }
  
  @Test
  def register_a_mx_mbean() {
    val simple = new Simple
    exporter.export(simple)
    assertEquals(simple.mxOperation, invoke[Simple]("mxOperation"))
    server.unregisterMBean(objectName[Simple])
  }
  
  @Test
  def change_the_already_exists_behavior() {
    val conf1 = new Configuration
    val exporter = new MBeanExporter(ifAlreadyExists=IfAlreadyExists.Replace)
    exporter.export(conf1)
    val conf2 = new Configuration
    conf2.size = 2
    exporter.export(conf2)
    assertEquals(conf2.size, invoke[Configuration]("size"))
    server.unregisterMBean(objectName[Configuration])
  }

  @Test
  def remove_object_from_jmx() {
    val conf = new Configuration

    try { exporter.remove(conf); fail("Should throw an InstanceNotFoundException") }
    catch { case e: InstanceNotFoundException => }

    exporter.export(conf)
    assertTrue(server.isRegistered(objectName[Configuration]))

    exporter.remove(conf)
    assertFalse(server.isRegistered(objectName[Configuration]))
  }

  // -- Test helpers ----------------------------------------------------------
  
  private def invoke[T](operationName: String)(implicit m: Manifest[T]) = {
    server.invoke(objectName(m), operationName, Array(), Array())
  }
  
  private def attribute[T](name: String)(implicit m: Manifest[T]) = {
    server.getAttribute(objectName(m), name)
  }
  
  private def objectName[T](implicit m: Manifest[T]) = exporter.namingStrategy(m.runtimeClass)
  
  // -- Test classes ----------------------------------------------------------
  
  class Configuration {
    @Managed var size: Int = 10
    @Managed def reload = true
  }
  
  trait StandardMBean { def operation: String }
  class Standard extends StandardMBean { def operation = "success" }
  
  trait SimpleMXBean { def mxOperation: String }
  class Simple extends SimpleMXBean { def mxOperation = "success" }
}