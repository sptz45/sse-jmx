/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import scala.annotation.target.field

package object export {
  
  /** The Managed annotation, annotated with {@literal @field}. */
  type Managed = annotation.Managed @field
  
  /** The ManagedResource annotation, annotated with {@literal @field}. */
  type ManagedResource = annotation.ManagedResource @field
}