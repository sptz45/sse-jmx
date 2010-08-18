/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.ObjectName
import annotation.{ManagedResource => JmxResource} /* unnecessary, but avoids Eclipse bug :-( */

/**
 * Derive an object name from the {@code objectName} value of the
 * {@literal @ManagedResource} annotation.
 */
private object AnnotationNamingStrategy extends ObjectNamingStrategy {
  
  /**
   * Supports only classes that contains a non-empty string in the
   * {@code objectName} value of a {@literal @ManagedResource} annotation.
   */
  def canCreateNameFor(clazz: Class[_]) = { 
    if (clazz.isAnnotationPresent(classOf[JmxResource]))
      clazz.getAnnotation(classOf[JmxResource]).objectName != ""
    else
      false
  }
  
  /**
   * Create an {@code ObjectName} using the {@code objectName} value
   * of the {@literal @ManagedResource} annotation that is present in
   * the specified class.
   */
  def nameFor(clazz: Class[_]): ObjectName = {
    val managedResource = clazz.getAnnotation(classOf[JmxResource])
    assert(managedResource != null)
    new ObjectName(managedResource.objectName)
  }
}
