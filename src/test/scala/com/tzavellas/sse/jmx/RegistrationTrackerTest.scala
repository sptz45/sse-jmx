package com.tzavellas.sse.jmx

import java.lang.management.ManagementFactory
import org.junit.After
import org.junit.Test

class RegistrationTrackerTest extends AbstractMBeanRegistrationTest {
  
  object registrar extends MBeanRegistrationSupport with MBeanRegistrationTracker {
    val server = ManagementFactory.getPlatformMBeanServer
  }

  def server = registrar.server

  @Test
  def unregisterAll_remove_all_registered_mbeans_from_server() {
    registrar.registerMBean(mbean, objectName)
    assertRegistered(mbean)
    
    registrar.unregisterAll()
    assertNotRegistered(mbean)
  }
  
  @Test
  def unregisterAll_ignores_missing_mbeans() {
    registrar.registerMBean(mbean, objectName)
    assertRegistered(mbean)
    
    // unregister bypassing the registrar
    registrar.server.unregisterMBean(objectName)
    assertNotRegistered(mbean)
    
    registrar.unregisterAll() // does not throw exception
  }
}