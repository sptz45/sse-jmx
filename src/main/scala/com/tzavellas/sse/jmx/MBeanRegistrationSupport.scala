package com.tzavellas.sse.jmx

import javax.management._

/**
 * A trait that provides a robust way for registering MBeans.
 * 
 * @see RegistrationBehavior
 */
trait MBeanRegistrationSupport {
  
  import IfAlreadyExists._

  /**
   * The <code>MBeanServer</code> that will be used for registering/unregistering
   * any MBeans.
   */
  val server: MBeanServer
  
  /**
   * Register the specified MBeans
   * 
   * @param mbean    the MBean to register
   * @param name     the ObjectName that will be used for registering the MBean
   * @param behavior what to do if an MBean with the same ObjectName is already registered 
   */
  def registerMBean(mbean: AnyRef, name: ObjectName, behavior: IfAlreadyExists.Enum = Fail) {
    if (server.isRegistered(name)) behavior match {
      case Ignore  => return
      case Fail    => throw new InstanceAlreadyExistsException(name.toString)
      case Replace =>
        try { server.unregisterMBean(name) }
        catch { case ignored: InstanceNotFoundException => () }
    }
    server.registerMBean(mbean, name)
  }

  /**
   * Unregister the MBean with the specified ObjectName from JMX.
   * 
   * @param name the ObjectName of the MBean
   */
  def unregisterMBean(name: ObjectName) {
    server.unregisterMBean(name)
  }
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
  
  /** Fail by throwing an {@code InstanceAlreadyExistsException} */
  val Fail = Value
}
