/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import javax.management._

/**
 * A trait that provides a robust way for registering MBeans.
 * 
 * The main benefit of this trait is that it provides an easy way of handling
 * what happens when an MBean with the same name is already registered in the
 * MBean server.
 * 
 * @see [[IfAlreadyExists]]
 */
trait MBeanRegistrationSupport {
  
  import IfAlreadyExists._

  /** The `MBeanServer` that will be used for registering the MBeans. */
  def server: MBeanServer
  
  /**
   * Register the specified MBean.
   * 
   * @param mbean    the MBean to register
   * @param name     the `ObjectName` that will be used for registering the MBean
   * @param behavior what to do if an MBean with the same `ObjectName` is already registered 
   */
  def registerMBean(mbean: AnyRef, name: ObjectName, behavior: IfAlreadyExists.Enum = Fail): Unit = {
    if (server.isRegistered(name)) behavior match {
      case Ignore  => return
      case Fail    => throw new InstanceAlreadyExistsException(name.toString)
      case Replace =>
        try   { server.unregisterMBean(name) }
        catch { case _: InstanceNotFoundException => () }
    }
    server.registerMBean(mbean, name)
  }

  /**
   * Unregister the MBean with the specified `ObjectName`.
   * 
   * @param name   the `ObjectName` of the MBean to unregister
   * @param ignoreErrors do not throw exception if an MBean with the specified
   *               `ObjectName` is not found.
   */
  def unregisterMBean(name: ObjectName, ignoreErrors: Boolean = false): Unit =
    try { server.unregisterMBean(name) }
    catch { case _: InstanceNotFoundException if ignoreErrors =>  }
}


/**
 * What to do when registering an MBean and another MBean with the same ObjectName
 * is already registered.
 */
object IfAlreadyExists extends Enumeration {  
  type Enum = Value
  
  /** Replace the existing MBean. */
  val Replace = Value
  
  /** Ignore the request to register this MBean leaving the existing MBean registered. */
  val Ignore  = Value
  
  /** Fail by throwing an `InstanceAlreadyExistsException` */
  val Fail = Value
}
