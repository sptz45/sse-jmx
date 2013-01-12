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

/**
 * Creates proxies for accessing MBeans.
 */
trait MBeanProxyFactory {

  /** The connection to the MBean server. */
  def server: MBeanServerConnection

  /**
   * A function to derive an `ObjectName` from a class when no `ObjectName`
   * is specified.
   */
  def namingStrategy: export.ObjectNamingStrategy

  /**
   * Create a proxy for accessing an standard MBean or an MXBean.
   *
   * @param name the object name (if `null` then the ObjectName is derived using
   *             [[namingStrategy]] function).
   * @tparam T   the interface of the MBean (must end in ''MBean'' or ''MXBean'').
   * @return     a proxy that implements T.
   */
  def proxyOf[T: Manifest](name: ObjectName = null): T = {
    val interface = manifest[T].runtimeClass
    lazy val objectName = if (name ne null) name else namingStrategy(interface)
    if (JmxUtils.isStandardMBeanInterface(interface)) {
      JMX.newMBeanProxy(server, objectName, interface).asInstanceOf[T]
    } else if (JmxUtils.isMXBeanInterface(interface)) {
      JMX.newMXBeanProxy(server, objectName, interface).asInstanceOf[T]
    } else {
      throw new IllegalArgumentException(s"${interface.getSimpleName} must be a standard MBean or a MXBean interface")
    }
  }

  /**
   * Create a proxy for accessing an MBean.
   *
   * In contrast to the [[proxyOf]] method, this method can be used to access
   * any MBean type including `DynamicMBean`.
   *
   * @param name the object name of the MBean.
   * @return     a proxy implementing `scala.Dynamic` that allows access
   *             to the mbean's attributes and operation.
   */
  def dynamicProxyOf(name: ObjectName): DynamicMBeanProxy = new DynamicMBeanProxy(server, name)
}

/**
 * A proxy for dynamically accessing the attributes and operations of an MBean.
 *
 * @param server the connection to the server where the MBean is registered.
 * @param name   the object name of the MBean.
 */
class DynamicMBeanProxy(server: MBeanServerConnection, name: ObjectName) extends Dynamic {

  /** Return the value of the specified attribute. */
  def selectDynamic(attribute: String) = server.getAttribute(name, attribute)

  //TODO introduce type parameter in selectDynamic.
  // The following implementation has problems with primitive types:
  //  def selectDynamic[A](attribute: String) = server.getAttribute(name, attribute).asInstanceOf[A]
  // When calling proxy.attr[Int] on an Int attribute we get:
  //  java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Long
  // Version 2.10.1 will have may scala.Dynamic bugfixes and we need to test this after upgrading.

  /** Update the value of the specified attribute. */
  def updateDynamic(attribute: String)(value: Any) {
    server.setAttribute(name, new Attribute(attribute, value))
  }

  /** Invoke the specified MBean operation. */
  def applyDynamic(operation: String)(args: Any*): Any = {
    val argClasses = args.map(_.getClass.getName).toArray
    val argsArray = args.toArray.asInstanceOf[Array[Object]]
    server.invoke(name, operation, argsArray, argClasses)
  }

  //TODO maybe implement applyDynamicNamed
  //def applyDynamicNamed(name: String)(args: (String, Any)*) {
}