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
   * The produced `ObjectName` is: ''domain-value:type=type-value'', where
   * ''domain-value'' is the package name and ''type-value'' is the simple
   * name of the specified class.
   *
   * If the specified `Class` object is an interface and ends in ''MBean'' or
   * ''MXBean'' then those suffixes are removed from the simple class name.
   *
   * This `PartialFunction` is applicable to all `Class` objects.
   */
  val useFullClassName: ObjectNamingStrategy = {
    case c => new ObjectName(s"${c.getPackage.getName}:type=${simpleNameOf(c)}")
  }
  
  /**
   * Use the simple class name to produce an `ObjectName`.
   *
   * The produced `ObjectName` is: ''domain-value:type=type-value'', where
   * ''domain-value'' is the specified `domain` argument and ''type-value''
   * is the simple name of the specified class.
   *
   * If the specified `Class` object is an interface and ends in ''MBean'' or
   * ''MXBean'' then those suffixes are removed from the simple class name.
   *
   * This `PartialFunction` is applicable to all `Class` objects.
   */
  def useSimpleClassName(domain: String): ObjectNamingStrategy = {
    case c => new ObjectName(s"${domain}:type=${simpleNameOf(c)}")
  } 

  /**
   * Produce an object name from the `objectName` property of the `ManagedResource`
   * annotation.
   *
   * This partial function is only applicable to classes that are annotated with
   * the [[ManagedResource]] annotation.
   */
  val useAnnotation: ObjectNamingStrategy = {
    case c if isAnnotationPresent(c) => getObjectNameFromAnnotation(c)
  }

  /**
   * Create an `ObjectName` from the `ManagedResource` annotation or from the
   * full class name.
   *
   * This partial function essentially combines the [[useAnnotation]] and
   * [[useFullClassName]] partial functions.
   */
  val default = useAnnotation orElse useFullClassName


  // -- private helper methods ------------------------------------------------

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
}