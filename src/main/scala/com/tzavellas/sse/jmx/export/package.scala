/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import scala.annotation.meta.field
import javax.management.ObjectName

/**
 * Provides classes that make it easy to export Scala objects to JMX.
 */
package object export {

  /** The Managed annotation, annotated with {@literal @field}. */
  type Managed = annotation.Managed @field

  /** The ManagedResource annotation, annotated with {@literal @field}. */
  type ManagedResource = annotation.ManagedResource @field

  /** A function used to construct an `ObjectName` from a `Class`. */
  type ObjectNamingStrategy = PartialFunction[Class[_], ObjectName]
}