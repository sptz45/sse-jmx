package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._


trait MBeanModelExtractor {
  
  def description(c: Class[_]): String = {
    val ann = c.getAnnotation(classOf[ManagedResource])
    if ((ann ne null) && ann.description != "")
      ann.description
    else
      c.getSimpleName
  }
  
  def attributes(c: Class[_]): Array[ModelMBeanAttributeInfo]
  
  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]): Array[ModelMBeanOperationInfo]
  
  def constructors(c: Class[_]): Array[ModelMBeanConstructorInfo] = Array()
  
  def notifications(c: Class[_]): Array[ModelMBeanNotificationInfo] = Array()
}
