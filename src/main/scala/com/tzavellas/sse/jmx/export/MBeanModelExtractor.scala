/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._
import com.tzavellas.sse.jmx.export.annotation.AnnotationReader


trait MBeanModelExtractor {

  def canExtractModel(c: Class[_]): Boolean

  def attributes(c: Class[_]): Array[ModelMBeanAttributeInfo]

  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]): Array[ModelMBeanOperationInfo]

  def constructors(c: Class[_]): Array[ModelMBeanConstructorInfo] = Array()

  def notifications(c: Class[_]): Array[ModelMBeanNotificationInfo] = Array()

  def description(c: Class[_]): String = {
    AnnotationReader.getResourceDescription(c).getOrElse(c.getSimpleName)
  }
}
