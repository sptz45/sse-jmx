package com.tzavellas.sse.jmx

object JmxUtils {
  
  def isStandardMBean(c: Class[_]) = hasMBeanInterface(c, "MBean")
  
  def isMXBean(c: Class[_]) = hasMBeanInterface(c, "MXBean")
  
  private def hasMBeanInterface(c: Class[_], interfaceNameSuffix: String) = {
    val expectedInterfaceName = c.getSimpleName + interfaceNameSuffix
    c.getInterfaces.exists(_.getSimpleName == expectedInterfaceName)
  }
}
