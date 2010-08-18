/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._

/**
 * Generates a {@code ModelMBeanInfo} from a class.
 */
trait MBeanInfoAssembler {
  
  this: MBeanModelExtractor =>
  
  def createMBeanInfo(clazz: Class[_]): ModelMBeanInfo = {
    val attrs = attributes(clazz)
    new ModelMBeanInfoSupport(clazz.getName,
                              description(clazz),
                              attrs,
                              constructors(clazz),
                              operations(clazz, attrs),
                              notifications(clazz))
  }
}
