/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import javax.management.{ ObjectName, InstanceNotFoundException }
import org.junit.{ Test, After }
import org.junit.Assert._
import com.tzavellas.sse.jmx.export.Managed

class MBeanProxyFactoyTest {

  private val factory = new MBeanProxyFactory

  private val exporter = new export.MBeanExporter
  private val objectName = new ObjectName("com.tzavellas.sse.jmx.test:type=Example")

  @After
  def unregisterMBean(): Unit = {
    exporter.unregisterMBean(objectName, ignoreErrors=true)
  }

  @Test(expected=classOf[IllegalArgumentException])
  def cannot_request_proxy_without_interface(): Unit = {
    factory.proxyOf[String]()
  }

  @Test(expected=classOf[IllegalArgumentException])
  def cannot_request_proxy_with_interface_not_mbean(): Unit = {
    factory.proxyOf[Runnable]()
  }

  @Test
  def request_an_mbean_proxy(): Unit = {
    val mbean = new Example1
    exporter.export(mbean, objectName)
    val proxy = factory.proxyOf[Example1MBean](objectName)
    assertEquals(mbean.operation, proxy.operation)
  }

  @Test
  def request_an_mbean_proxy_without_specifying_objectName(): Unit = {
    val mbean = new Example1
    try {
      exporter.export(mbean)
      val proxy = factory.proxyOf[Example1MBean]()
      assertEquals(mbean.operation, proxy.operation)
    } finally {
      try { exporter.remove(mbean) } catch { case e: InstanceNotFoundException => () }
    }
  }

  @Test
  def request_an_mxbean_proxy(): Unit = {
    val mbean = new Example2
    exporter.export(mbean, objectName)
    val proxy = factory.proxyOf[Example2MXBean](objectName)
    assertEquals(mbean.operation, proxy.operation)
  }

  @Test
  def test_dynamic_access_to_mbean(): Unit = {
    val mbean = new AnnotatedObject
    exporter.export(mbean, objectName)
    val proxy = factory.dynamicProxyOf(objectName)

    // Fails to compile: assertEquals(mbean.attr, proxy.attr[Int])
    assertEquals(mbean.attr, proxy.operation())
    proxy.attr = 1
    assertEquals(1, mbean.attr)
  }


  // -- test classes ----------------------------------------------------------

  class AnnotatedObject {
    @Managed var attr = 10
    @Managed def operation = attr
  }

  trait Example1MBean { def operation: Int }
  class Example1 extends Example1MBean { def operation = 1 }

  trait Example2MXBean { def operation: Int }
  class Example2 extends Example2MXBean { def operation = 2 }
}
