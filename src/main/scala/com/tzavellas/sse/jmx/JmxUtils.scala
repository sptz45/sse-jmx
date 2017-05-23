/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import javax.management.DynamicMBean

private object JmxUtils {

  def isStandardMBean(c: Class[_]): Boolean = hasMBeanInterface(c, "MBean")

  def isMXBean(c: Class[_]): Boolean = hasMBeanInterface(c, "MXBean")

  def isMBean(c: Class[_]): Boolean = isStandardMBean(c) || isMXBean(c) || c.getInterfaces.contains(classOf[DynamicMBean])

  def isStandardMBeanInterface(i: Class[_]): Boolean = i.isInterface && i.getSimpleName.endsWith("MBean")

  def isMXBeanInterface(i: Class[_]): Boolean = i.isInterface && i.getSimpleName.endsWith("MXBean")

  private def hasMBeanInterface(c: Class[_], interfaceNameSuffix: String) = {
    val expectedInterfaceName = c.getSimpleName + interfaceNameSuffix
    c.getInterfaces.exists(_.getSimpleName == expectedInterfaceName)
  }
}
