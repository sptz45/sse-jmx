package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * A strategy trait for creating <code>ObjectName</code>s from classes.
 * 
 * @see ObjectName
 */
trait ObjectNamingStrategy {
  
  /**
   * Whether this strategy can create an {@code ObjectName} for the specified
   * class.
   */
  def canCreateNameFor(clazz: Class[_]): Boolean
  
  /**
   * Create an <code>ObjectName</code> to be used when registering objects of the
   * specified class to JMX.
   * 
   * @param clazz the class of the object's that will be registered under the
   *              returned ObjectName.
   * 
   * @return the ObjectName
   * @throws MalformedObjectNameException if a valid ObjectName could not get
   *         created from the specified arguments.
   */
  def nameFor(clazz: Class[_]): ObjectName
}
