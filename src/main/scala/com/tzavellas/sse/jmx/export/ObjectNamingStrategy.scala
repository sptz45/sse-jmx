/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * A strategy trait for creating <code>ObjectName</code>s from classes.
 * 
 * @see ObjectName
 */
trait ObjectNamingStrategy {
  
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
