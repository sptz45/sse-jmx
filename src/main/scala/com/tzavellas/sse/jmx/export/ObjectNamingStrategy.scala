package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * A strategy trait for creating <code>ObjectName</code>s.
 * 
 * @see ObjectName
 */
trait ObjectNamingStrategy {
  
  def canCreateNameFor(clazz: Class[_]): Boolean
  
  /**
   * Create an <code>ObjectName</code> to be used when registering objects of the specified
   * class to JMX.
   * 
   * @param clazz the class of the object's that will be registered under the returned ObjectName
   * @param keyProperties a Map containing the key properties of the ObjectName
   * 
   * @return the ObjectName
   * @throws MalformedObjectNameException if a valid ObjectName could not get created from the
   *         specified arguments.
   *  
   * @see ObjectName
   */
  def nameFor(clazz: Class[_], keyProperties: Map[String, Any] = Map()): ObjectName
}
