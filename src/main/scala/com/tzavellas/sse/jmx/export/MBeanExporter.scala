package com.tzavellas.sse.jmx.export

import javax.management.ObjectName
import javax.management.modelmbean.{ModelMBeanInfo, RequiredModelMBean}
import com.tzavellas.sse.jmx.JmxUtils

trait MBeanExporter extends MBeanRegistrationSupport {
  
  val assembler = AnnotationMBeanInfoAssembler
  val namingStrategy = new DefaultNamingStrategy
  
  def register(ref: AnyRef) {
    register(ref, namingStrategy.nameFor(ref.getClass))
  }
  
  def register(ref: AnyRef, name: ObjectName) {
    if (JmxUtils.isStandardMBean(ref.getClass) || JmxUtils.isMXBean(ref.getClass))
      registerMBean(ref, name)
    else {
      val info = new NoGetterAndSetterMBeanInfo(assembler.createMBeanInfo(ref.getClass))
      val mbean = new RequiredModelMBean(info)
      mbean.setManagedResource(ref, "ObjectReference")
      registerMBean(mbean, name)
    }
  }
  
  def objectName(c: Class[_]) = {
    if (AnnotationNamingStrategy.canCreateNameFor(c))
      AnnotationNamingStrategy.nameFor(c)
    else
      namingStrategy.nameFor(c)
  }
}