/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import javax.management.InstanceNotFoundException
import javax.management.ObjectName
import org.junit.Assert._

abstract class AbstractMBeanRegistrationTest {
  
  def registrar: MBeanRegistrationSupport
  
  val mbean = new Simple
  val objectName = new ObjectName("com.tzavellas.sse.jmx.test:type=SimpleMBean")

  trait SimpleMBean { def operation: Int }
  
  class Simple(val id: Int = 0) extends SimpleMBean {
    def operation = id
  }
  
  def assertRegistered(mbean: Simple) {
    assertEquals(mbean.operation, registrar.server.invoke(objectName, "operation", Array(), Array()))
  }
  
  def assertNotRegistered(mbean: Simple) {
    try {
      val result = registrar.server.invoke(objectName, "operation", Array(), Array()).asInstanceOf[Int]
      assertTrue(mbean.operation != result)
    } catch {
      case e: InstanceNotFoundException =>
    }
  }
}