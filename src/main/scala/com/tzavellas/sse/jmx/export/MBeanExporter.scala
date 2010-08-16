package com.tzavellas.sse.jmx.export

import java.lang.management.ManagementFactory
import javax.management.{ObjectName, MBeanServer}
import javax.management.modelmbean.{ModelMBeanInfo, RequiredModelMBean}
import com.tzavellas.sse.jmx.MBeanRegistrationSupport 

class MBeanExporter (
  val server: MBeanServer = ManagementFactory.getPlatformMBeanServer)
    extends MBeanRegistrationSupport {
  
  val assembler = AnnotationMBeanInfoAssembler
  val namingStrategy = new DefaultNamingStrategy
  
  def export(ref: AnyRef) {
    export(ref, namingStrategy.nameFor(ref.getClass))
  }
  
  def export(ref: AnyRef, name: ObjectName) {
    
    def refIsAnMBean = JmxUtils.isStandardMBean(ref.getClass) || JmxUtils.isMXBean(ref.getClass)
    
    def modelMBean = {
      val info = new NoGetterAndSetterMBeanInfo(assembler.createMBeanInfo(ref.getClass))
      val mbean = new RequiredModelMBean(info)
      mbean.setManagedResource(ref, "ObjectReference")
      mbean
    }
    
    val mbean = if (refIsAnMBean) ref else modelMBean
    registerMBean(mbean, name)
  }
  
  def objectName(c: Class[_]) = {
    if (AnnotationNamingStrategy.canCreateNameFor(c))
      AnnotationNamingStrategy.nameFor(c)
    else
      namingStrategy.nameFor(c)
  }
}