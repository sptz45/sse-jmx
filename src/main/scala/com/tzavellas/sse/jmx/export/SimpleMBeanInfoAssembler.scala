package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._
import java.lang.reflect.Method

object SimpleMBeanInfoAssembler extends MBeanInfoAssembler with MBeanModelExtractor {
  
  val excludedAttributes = List("toString", "getClass", "clone", "hashCode")
  
  val excludedOperations = List("wait", "notify", "notifyAll", "equals")
  
  def attributes(c: Class[_]) = {
    val methods = c.getMethods
    val attrs =
      for (reader <- findVals(methods).filterNot(m => excludedAttributes.contains(m.getName)))
      yield {
        val desc = new DescriptorSupport
        desc.setField("name", reader.getName)
        desc.setField("descriptorType", "attribute")
        desc.setField("getMethod", reader.getName)
        
        val writerName = reader.getName + "_$eq"
        val hasWriter = methods.find(_.getName == writerName) != None
        if (hasWriter) desc.setField("setMethod", writerName)
        
        new ModelMBeanAttributeInfo(reader.getName, // name
                                    reader.getReturnType.getName, 
                                    reader.getName, // description
                                    true,
                                    hasWriter,
                                    false,
                                    desc)
      }
    attrs.toArray[ModelMBeanAttributeInfo]
  }
  
  private def findVals(methods: Array[Method]) =
    methods.filter(m =>m.getParameterTypes.isEmpty && m.getReturnType != classOf[Unit])
  
  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]) = {
    c.getMethods
     .filterNot(m => excludedOperations.contains(m.getName))
     .filter(m => isOperation(m) || isAttributeMethod(m, attrs))
     .map(m => new ModelMBeanOperationInfo(m.getName, m))
  }
  
  private def isOperation(m: Method) = m.getParameterTypes.length == 0
  
  private def isAttributeMethod(m: Method, attrs: Array[ModelMBeanAttributeInfo]) = {
    val name = m.getName().replaceAll("_$eq", "")
    attrs.find(_.getName == name) != None
  }
}
