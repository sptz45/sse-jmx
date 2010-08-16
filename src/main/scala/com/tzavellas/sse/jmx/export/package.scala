package com.tzavellas.sse.jmx

import scala.annotation.target.field

package object export {
  
  /** The Managed annotation, annotated with {@literal @field}. */
  type Managed = annotation.Managed @field
  
  /** The ManagedResource annotation, annotated with {@literal @field}. */
  type ManagedResource = annotation.ManagedResource @field
}