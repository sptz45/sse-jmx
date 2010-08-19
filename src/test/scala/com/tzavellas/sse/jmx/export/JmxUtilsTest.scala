/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._

class JmxUtilsTest {

  @Test
  def should_detect_an_mbean() {
    assertTrue(JmxUtils.isStandardMBean(classOf[Helper]))
    assertFalse(JmxUtils.isStandardMBean(classOf[String]))
  }
  
  @Test
  def should_detect_an_mxbean() {
    assertTrue(JmxUtils.isMXBean(classOf[Helper]))
    assertFalse(JmxUtils.isMXBean(classOf[String]))
  }
  
  trait HelperMBean
  trait HelperMXBean
  class Helper extends HelperMBean with HelperMXBean
}
