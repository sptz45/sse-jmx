package com.tzavellas.sse.jmx

import scala.annotation.target.field

package object export {
  
  type Managed = annotation.Managed @field
  
  type ManagedResource = annotation.ManagedResource @field
}