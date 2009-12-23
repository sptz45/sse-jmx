package com.tzavellas.sse.jmx.export

import org.junit.{Test, After}
import org.junit.Assert._
import javax.management._

class MBeanRegistrationSupportTest {
  
  import MBeanRegistrationSupportTest._
  
  private val mbean = new Simple(0)
  private val objectName = new ObjectName("com.tzavellas.sse.jmx.test:type=SimpleMBean")
  
  @After
  def unregisterMBean() {
    registrar.unregisterMBean(objectName)
  }

  @Test
  def registration_of_a_standard_mbean() {
    registrar.registerMBean(mbean, objectName)
    assertMBeanIsRegistered(mbean)
  }
  
  @Test(expected=classOf[InstanceAlreadyExistsException])
  def when_behavior_fail_then_fail_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    assertMBeanIsRegistered(mbean)
    registrar.registerMBean(mbean, objectName)
  }
  
  @Test
  def when_behavior_ignore_no_registration_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    assertMBeanIsRegistered(mbean)
    
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, RegistrationBehavior.Ignore)
    assertMBeanIsRegistered(mbean) // the first mbean remains registered
  }
  
  @Test
  def when_behavior_replace_mbean_gets_replaced() {
    registrar.registerMBean(mbean, objectName)
    assertMBeanIsRegistered(mbean)
    
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, RegistrationBehavior.Replace)
    assertMBeanIsRegistered(mbean1)
  }
  
  def assertMBeanIsRegistered(mbean: Simple) {
    assertEquals(mbean.operation, registrar.server.invoke(objectName, "operation", Array(), Array()))
  }
}

object MBeanRegistrationSupportTest {
  
  object registrar extends MBeanRegistrationSupport with PlatformMBeanServer

  trait SimpleMBean {
    def operation: Int
  }
  class Simple(val id: Int = 0) extends SimpleMBean {
    def operation = id
  }
}