package com.tzavellas.sse.jmx.export

import java.lang.management.ManagementFactory
import javax.management.{ObjectName, MBeanServer}
import javax.management.modelmbean.RequiredModelMBean
import com.tzavellas.sse.jmx.MBeanRegistrationSupport 

class MBeanExporter (
  val server: MBeanServer = ManagementFactory.getPlatformMBeanServer,
  val namingStrategy: ObjectNamingStrategy = new DefaultNamingStrategy)
    extends MBeanRegistrationSupport {
  
  val assembler = AnnotationMBeanInfoAssembler
  
  
  def export(ref: AnyRef) {
    export(ref, objectName(ref.getClass))
  }
  
  def export(ref: AnyRef, name: ObjectName) {
    
    def refIsAnMBean =
      JmxUtils.isStandardMBean(ref.getClass) || JmxUtils.isMXBean(ref.getClass)
    
    def modelMBean = {
      val info = new NoGetterAndSetterMBeanInfo(assembler.createMBeanInfo(ref.getClass))
      val model = new RequiredModelMBean(info)
      model.setManagedResource(ref, "ObjectReference")
      model
    }
    
    val mbean = if (refIsAnMBean) ref else modelMBean
    registerMBean(mbean, name)
  }
  
  def objectName(c: Class[_]): ObjectName = {
    if (AnnotationNamingStrategy.canCreateNameFor(c))
      AnnotationNamingStrategy.nameFor(c)
    else
      namingStrategy.nameFor(c)
  }
}