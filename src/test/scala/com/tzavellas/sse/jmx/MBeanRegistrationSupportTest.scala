/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import org.junit.{Test, After}
import org.junit.Assert._
import java.lang.management.ManagementFactory
import javax.management._

class MBeanRegistrationSupportTest extends AbstractMBeanRegistrationTest {
  
  object registrar extends MBeanRegistrationSupport {
    val server = ManagementFactory.getPlatformMBeanServer
  }

  def server = registrar.server
  
  @After
  def unregisterMBean() {
    try { server.unregisterMBean(objectName) } catch { case e: InstanceNotFoundException => }
    assertNotRegistered(mbean)
  }

  @Test
  def registration_of_a_standard_mbean() {
    registrar.registerMBean(mbean, objectName)
    assertRegistered(mbean)
  }
  
  @Test(expected=classOf[InstanceAlreadyExistsException])
  def when_behavior_is_fail_then_fail_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName)

    assertNotRegistered(mbean1)
    assertRegistered(mbean)
  }
  
  @Test
  def when_behavior_ignore_no_registration_if_mbean_already_exists() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, IfAlreadyExists.Ignore)

    assertNotRegistered(mbean1)
    assertRegistered(mbean)
  }
  
  @Test
  def when_behavior_is_replace_the_mbean_gets_replaced() {
    registrar.registerMBean(mbean, objectName)
    val mbean1 = new Simple(1)
    registrar.registerMBean(mbean1, objectName, IfAlreadyExists.Replace)
    
    assertRegistered(mbean1)
    assertNotRegistered(mbean)
  }

  @Test
  def unregister_mbean() {
    registrar.registerMBean(mbean, objectName)
    assertRegistered(mbean)
    registrar.unregisterMBean(objectName)
    assertNotRegistered(mbean)
  }

  @Test(expected=classOf[InstanceNotFoundException])
  def exception_if_not_registered() {
    registrar.unregisterMBean(objectName)
  }

  @Test
  def ignore_exception_if_not_registered() {
    registrar.unregisterMBean(objectName, ignore=true)
  }
}