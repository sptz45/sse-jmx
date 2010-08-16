package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._

trait MBeanModelExtractor {
  
  def description(c: Class[_]): String =
    c.getAnnotation(classOf[ManagedResource]) match {
      case mr: annotation.ManagedResource if mr.description != "" => mr.description
      case _ => c.getSimpleName
    }
  
  def attributes(c: Class[_]): Array[ModelMBeanAttributeInfo]
  
  def constructors(c: Class[_]): Array[ModelMBeanConstructorInfo] = Array()
  
  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]): Array[ModelMBeanOperationInfo]
  
  def notifications(c: Class[_]): Array[ModelMBeanNotificationInfo] = Array()
}
