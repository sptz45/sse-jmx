/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * Contains functions for creating an `ObjectName`s from classes.
 */
object ObjectNamingStrategies {

  /**
   * Use the full class name to produce an `ObjectName`.
   *
   * The produced `ObjectName` is: <i>domain-value:type=type-value</i>, where
   * <i>domain-value</i> is the package name and <i>type-value</i> is the simple
   * name of the specified class.
   */
  val useFullClassName: ObjectNamingStrategy = {
    case c => new ObjectName(s"${c.getPackage.getName}:type=${simpleNameOf(c)}")
  }
  
  /**
   * Use the simple class name to produce an `ObjectName`.
   *
   * The produced `ObjectName` is: <i>domain-value:type=type-value</i>, where
   * <i>domain-value</i> is the specified `domain` argument and <i>type-value</i>
   * is the simple name of the specified class.
   */
  def useSimpleClassName(domain: String): ObjectNamingStrategy = {
    case c => new ObjectName(s"${domain}:type=${simpleNameOf(c)}")
  } 

  /**
   * Produce an object name from the `objectName` property of the `ManagedResource`
   * annotation.
   * 
   * This partial function is only applicable to classes that are annotated with
   * the `ManagedResource` annotation. 
   */
  val useAnnotation: ObjectNamingStrategy = {
    case c if isAnnotationPresent(c) => getObjectNameFromAnnotation(c)
  }

  private def isAnnotationPresent(c: Class[_]) = {
    Option(c.getAnnotation(classOf[ManagedResource])).exists(_.objectName != "")
  }

  private def getObjectNameFromAnnotation(c: Class[_]) = {
    new ObjectName(c.getAnnotation(classOf[ManagedResource]).objectName)
  }
  
  private def simpleNameOf(c: Class[_]): String = {
    if (c.isInterface) {
      if (c.getSimpleName.endsWith("MBean")) return c.getSimpleName.dropRight(5)
      if (c.getSimpleName.endsWith("MXBean")) return c.getSimpleName.dropRight(6)
    }
    c.getSimpleName
  }

  /**
   * Create an `ObjectName` from the `ManagedResource` annotation or from the
   * full class name.
   * 
   * This partial function essentially combines the [[useAnnotation]] and
   * [[useFullClassName]] partial functions.
   */
  val default = useAnnotation orElse useFullClassName
}