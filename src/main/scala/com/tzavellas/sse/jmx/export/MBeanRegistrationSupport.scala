package com.tzavellas.sse.jmx.export

import javax.management._

/**
 * A trait that provides a robust way for registering MBeans.
 * 
 * @author spiros
 */
trait MBeanRegistrationSupport {
  
  import RegistrationBehavior._

  /**
   * The <code>MBeanServer</code> that will be used for registering/unregistering
   * any MBeans.
   */
  val server: MBeanServer
  
  /**
   * Register the specified MBeans
   * 
   * @param mbean the MBean to register
   * @param name  the ObjectName that will be used for registering the MBean
   * @param ifRegistered what to do if an MBean with the same ObjectName is already registered
   * 
   * @see RegistrationBehavior
   */
  def registerMBean(mbean: AnyRef, name: ObjectName, ifRegistered: RegistrationBehavior.Enum = Fail) {
    if (server.isRegistered(name)) ifRegistered match {
      case Ignore => return
      case Fail => throw new InstanceAlreadyExistsException(name.toString)
      case Replace =>
        try { server.unregisterMBean(name) }
        catch { case ignored: InstanceNotFoundException => () }
    }
    server.registerMBean(mbean, name)
  }

  /**
   * Unregister the MBean with the specified ObjectName from JMX.
   */
  def unregisterMBean(name: ObjectName) {
    server.unregisterMBean(name)
  }
}

/**
 * A module defining an enumeration of the behavior to take when an MBean with the same
 * ObjectName is already registered.
 */
object RegistrationBehavior extends Enumeration {
  
  /**
   * What to do if an MBean with the same ObjectName is already registered
   */
  type Enum = Value
  
  /**
   * Replace the existing MBean
   */
  val Replace = Value
  
  /**
   * Ignore the request to register this MBean leaving the existing MBean registered
   */
  val Ignore  = Value
  
  /**
   * Fail by throwing an <code>InstanceAlreadyExistsException</code>
   */
  val Fail = Value
}
