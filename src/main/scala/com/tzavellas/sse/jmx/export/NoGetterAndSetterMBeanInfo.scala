/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.MBeanOperationInfo
import javax.management.modelmbean._

/**
 * A hack to remove getters and setters from Model MBean operations.
 * 
 * <p>This is a hack to fix bug: <i>RFE 6339571</i>.</p> 
 * 
 * @see http://weblogs.java.net/blog/2007/02/13/removing-getters-model-mbean-operations
 */
private class NoGetterAndSetterMBeanInfo(info: ModelMBeanInfo) extends ModelMBeanInfoSupport(info) {
    
  override def clone() = new NoGetterAndSetterMBeanInfo(this)
  
  private def writeReplace(): AnyRef = {
    
    def isGetterOrSetter(role: String) = "getter".equalsIgnoreCase(role) || "setter".equalsIgnoreCase(role)
    
    def role(info: MBeanOperationInfo) = info.getDescriptor.getFieldValue("role").asInstanceOf[String]

    val operationsWithNoGettersOrSetters = getOperations.collect {
      case info if !isGetterOrSetter(role(info)) => info.asInstanceOf[ModelMBeanOperationInfo]
    }
      
    return new ModelMBeanInfoSupport(
      getClassName,
      getDescription,
      getAttributes.asInstanceOf[Array[ModelMBeanAttributeInfo]],
      getConstructors.asInstanceOf[Array[ModelMBeanConstructorInfo]],
      operationsWithNoGettersOrSetters,
      getNotifications.asInstanceOf[Array[ModelMBeanNotificationInfo]],
      getMBeanDescriptor)
  }
}
