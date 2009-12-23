package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

object AnnotationNamingStrategy extends ObjectNamingStrategy {
  
  def canCreateNameFor(clazz: Class[_]) = { 
    if (clazz.isAnnotationPresent(classOf[ManagedResource]))
      clazz.getAnnotation(classOf[ManagedResource]).objectName != ""
    else
      false
  }
  
  def nameFor(clazz: Class[_], keyProperties: Map[String, Any]): ObjectName = {
    val managedResource = clazz.getAnnotation(classOf[ManagedResource])
    assert(managedResource != null)
    
    new ObjectName(managedResource.objectName)
  }
}
