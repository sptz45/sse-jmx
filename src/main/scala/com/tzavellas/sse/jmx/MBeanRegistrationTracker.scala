/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import scala.collection.mutable.{HashSet, SynchronizedSet}
import javax.management.{ObjectName, InstanceNotFoundException}

/**
 * Tracks MBean registrations.
 * 
 * @see [[unregisterAll]]
 */
trait MBeanRegistrationTracker extends MBeanRegistrationSupport {

  private val registered = new HashSet[ObjectName] with SynchronizedSet[ObjectName]

  abstract override def registerMBean(mbean: AnyRef, name: ObjectName, behavior: IfAlreadyExists.Enum = IfAlreadyExists.Fail) {
    super.registerMBean(mbean, name, behavior)
    registered += name
  }

  abstract override def unregisterMBean(name: ObjectName, ignore: Boolean = false) {
    super.unregisterMBean(name)
    registered -= name
  }

  /**
   * Unregister all the MBeans that have been registered using this instance.
   *
   * @see [[registerMBean]]
   */
  def unregisterAll() {
    for (name <- registered) yield {
      try { unregisterMBean(name) }
      catch { case e: InstanceNotFoundException => }
    }
  }
}