/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import java.lang.reflect.Method
import javax.management.modelmbean._
import com.tzavellas.sse.jmx.export.annotation.AnnotationReader

/**
 * Creates {@code ModelMBeanInfo} from classes that are annotated with the
 * {@code Managed} annotation.
 * 
 * <p>Annotated <strong>def</strong>s become <em>operations</em>, annotated
 * <strong>var</strong>s become <em>attributes</em> and annotated
 * <strong>val</strong>s become <em>read-only attributes</em>.</p>
 * 
 * @see Managed
 * @see ManagedResource
 */ 
object AnnotationMBeanInfoAssembler extends MBeanInfoAssembler
                                       with MBeanModelExtractor {
  
  def attributes(c: Class[_]) = {
    val attrs =
      for (field <- c.getDeclaredFields if AnnotationReader.hasManagedAnnotation(field))
      yield {
        val desc = new DescriptorSupport
        val supportsWriting = hasWriter(c, field.getName) && !AnnotationReader.getReadOnly(field)
        val description = AnnotationReader.getDescription(field).getOrElse(field.getName)
        
        desc.setField("name", field.getName)
        desc.setField("descriptorType", "attribute")
        desc.setField("getMethod", field.getName)
        if (supportsWriting) desc.setField("setMethod", field.getName + "_$eq")
        for (timeLimit <- translateTimeLimit(AnnotationReader.getCurrencyTimeLimit(field)))
          desc.setField("currencyTimeLimit", timeLimit)
        
        new ModelMBeanAttributeInfo(field.getName, field.getType.getName, description,
                                    true, supportsWriting, false, desc)
      }
    attrs.toArray[ModelMBeanAttributeInfo]
  }
  
  private def hasWriter(c: Class[_], fieldName: String) =
    c.getMethods().exists(_.getName == fieldName+"_$eq")
    
  
  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]) = {
    def isOperation(m: Method) = AnnotationReader.hasManagedAnnotation(m)
    def isAttributeMethod(m: Method) = attrs.exists { attr =>
      attr.getDescriptor.getFieldValue("getMethod") == m.getName ||
      attr.getDescriptor.getFieldValue("setMethod") == m.getName
    }
    def roleOf(m: Method): String = {
      if (attrs.exists(_.getDescriptor.getFieldValue("getMethod") == m.getName)) return "getter"
      if (attrs.exists(_.getDescriptor.getFieldValue("setMethod") == m.getName)) return "setter"
      "operation"
    }
    c.getMethods.collect {
      case m if isOperation(m) || isAttributeMethod(m) => createOperationInfo(m, roleOf(m))
    }
  }
  
  private def createOperationInfo(method: Method, role: String): ModelMBeanOperationInfo = {
    def description = AnnotationReader.getDescription(method).getOrElse(method.getName)
    val desc = new DescriptorSupport 
    desc.setField("name", method.getName)
    desc.setField("descriptorType", "operation")
    desc.setField("role", role)
    for (timeLimit <- translateTimeLimit(AnnotationReader.getCurrencyTimeLimit(method)))
      desc.setField("currencyTimeLimit", timeLimit)
    new ModelMBeanOperationInfo(description, method, desc)
  }
  
  private def translateTimeLimit(limit: Option[Int]) = {
    CurrencyTimeLimitTranslator.translate(limit)
  }
}


private object CurrencyTimeLimitTranslator {  
  
  private val decade = 315360000
  
  def translate(seconds: Option[Int]) = seconds match {
    case None => None
    case Some(x) if x == 0 => Some(decade)
    case s                 => s
  }   
}
