/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import language.dynamics
import javax.management.ObjectName
import javax.management.MBeanServerConnection
import javax.management.JMX
import javax.management.Attribute

trait MBeanProxyFactory {

  def server: MBeanServerConnection
  def namingStrategy: export.ObjectNamingStrategy
  
  def proxyOf[T: Manifest](name: ObjectName = null): T = {
    val interface = manifest[T].runtimeClass
    val objectName = if (name ne null) name else namingStrategy(interface)
    
    if (JmxUtils.isStandardMBeanInterface(interface)) {
      JMX.newMBeanProxy(server, name, interface).asInstanceOf[T]
    } else if (JmxUtils.isMXBeanInterface(interface)) {
      JMX.newMXBeanProxy(server, name, interface).asInstanceOf[T]
    } else {
      throw new IllegalArgumentException(s"${interface.getSimpleName} must be a standard MBean or a MXBean interface")
    }
  }

  def dynamicProxyOf(name: ObjectName) = new DynamicMBeanProxy(server, name)
}

class DynamicMBeanProxy(server: MBeanServerConnection, name: ObjectName) extends Dynamic {

  def selectDynamic(attribute: String) = server.getAttribute(name, attribute)
  
  def updateDynamic(attribute: String)(value: Any) {
    server.setAttribute(name, new Attribute(attribute, value))
  }

  def applyDynamic(operation: String)(args: Any*): Any = {
    val argClasses = args.map(_.getClass.getName).toArray
    val argsArray = args.toArray.asInstanceOf[Array[Object]]
    server.invoke(name, operation, argsArray, argClasses)
  }

  //TODO maybe implement applyDynamicNamed
  //def applyDynamicNamed(name: String)(args: (String, Any)*) {
}