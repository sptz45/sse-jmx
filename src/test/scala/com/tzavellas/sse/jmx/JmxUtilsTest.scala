/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import javax.management.StandardMBean

import org.junit.Test
import org.junit.Assert._

class JmxUtilsTest {

  @Test
  def should_detect_an_mbean(): Unit = {
    assertTrue(JmxUtils.isStandardMBean(classOf[Helper]))
    assertFalse(JmxUtils.isStandardMBean(classOf[String]))
  }
  
  @Test
  def should_detect_a_standard_mbean_interface(): Unit = {
    assertTrue(JmxUtils.isStandardMBeanInterface(classOf[HelperMBean]))
    assertFalse(JmxUtils.isStandardMBeanInterface(classOf[HelperMXBean]))
    assertFalse(JmxUtils.isStandardMBeanInterface(classOf[ClassMBean]))
    assertFalse(JmxUtils.isStandardMBeanInterface(classOf[Helper])) // not interface
  }
  
  @Test
  def should_detect_an_mxbean(): Unit = {
    assertTrue(JmxUtils.isMXBean(classOf[Helper]))
    assertFalse(JmxUtils.isMXBean(classOf[String]))
  }
  
  @Test
  def should_detect_a_standard_mxbean_interface(): Unit = {
    assertTrue(JmxUtils.isMXBeanInterface(classOf[HelperMXBean]))
    assertFalse(JmxUtils.isMXBeanInterface(classOf[HelperMBean]))
    assertFalse(JmxUtils.isMXBeanInterface(classOf[ClassMXBean]))
    assertFalse(JmxUtils.isMXBeanInterface(classOf[Helper])) // not interface
  }

  @Test
  def should_detect_a_dynamic_mbean(): Unit = {
    assertTrue(JmxUtils.isMBean(classOf[StandardMBean]))
  }
  
  trait HelperMBean
  trait HelperMXBean
  class Helper extends HelperMBean with HelperMXBean
  
  class ClassMBean
  class ClassMXBean
}
