/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import org.junit.{Test, After}
import org.junit.Assert._
import java.lang.management.ManagementFactory
import javax.management._

class MBeanRegistrationSupportTest {
  
  import MBeanRegistrationSupportTest._
  
  private val mbean = new Simple
  
  @After
  def unregisterMBean() {
    registrar.unregisterMBean(objectName)
    assertNotRegistered(mbean)
  }

  @Test
  def registration_of_a_standard_mbean() {
    registrar.registerMBean(mbean, objectName)
    assertIsRegistered(mbean)
  }
  
  @Test(expected=classOf[InstanceAlreadyExistsException])
  def when_behavior_is_fail_then_fail_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName)

    assertNotRegistered(mbean1)
    assertIsRegistered(mbean)
  }
  
  @Test
  def when_behavior_ignore_no_registration_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, IfAlreadyExists.Ignore)

    assertNotRegistered(mbean1)
    assertIsRegistered(mbean)
  }
  
  @Test
  def when_behavior_is_replace_the_mbean_gets_replaced() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, IfAlreadyExists.Replace)
    
    assertIsRegistered(mbean1)
    assertNotRegistered(mbean)
  }
}

object MBeanRegistrationSupportTest {
  
  object registrar extends MBeanRegistrationSupport {
    val server = ManagementFactory.getPlatformMBeanServer
  }
  
  val objectName = new ObjectName("com.tzavellas.sse.jmx.test:type=SimpleMBean")

  trait SimpleMBean { def operation: Int }
  
  class Simple(val id: Int = 0) extends SimpleMBean {
    def operation = id
  }
  
  def assertIsRegistered(mbean: Simple) {
    assertEquals(mbean.operation, registrar.server.invoke(objectName, "operation", Array(), Array()))
  }
  
  def assertNotRegistered(mbean: Simple) {
    try {
      val result = registrar.server.invoke(objectName, "operation", Array(), Array()).asInstanceOf[Int]
      assertTrue(mbean.operation != result)
    } catch {
      case e: InstanceNotFoundException => ()
    }
  }
}