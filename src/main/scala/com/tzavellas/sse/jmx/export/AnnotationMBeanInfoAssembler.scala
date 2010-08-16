package com.tzavellas.sse.jmx.export

import java.lang.reflect.Method
import javax.management.modelmbean._

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
      for (field <- c.getDeclaredFields if field.isAnnotationPresent(classOf[Managed]))
      yield {
        val managed = field.getAnnotation(classOf[Managed])
        val desc = new DescriptorSupport
        val supportsWriting = hasWriter(c, field.getName) && !managed.readOnly
        val description = if (managed.description == "") field.getName else managed.description
        
        desc.setField("name", field.getName)
        desc.setField("descriptorType", "attribute")
        desc.setField("getMethod", field.getName)
        if (supportsWriting) desc.setField("setMethod", field.getName + "_$eq")
        for (timeLimit <- translateTimeLimit(managed))
          desc.setField("currencyTimeLimit", timeLimit)
        
        new ModelMBeanAttributeInfo(field.getName, field.getType.getName, description,
                                    true, supportsWriting, false, desc)
      }
    attrs.toArray[ModelMBeanAttributeInfo]
  }
  
  private def hasWriter(c: Class[_], fieldName: String) =
    c.getMethods().exists(_.getName == fieldName+"_$eq")
    
  
  def operations(c: Class[_], attrs: Array[ModelMBeanAttributeInfo]) = {
    def isOperation(m: Method) = m.isAnnotationPresent(classOf[Managed])
    def isAttributeMethod(m: Method) = attrs.exists { attr =>
      attr.getDescriptor.getFieldValue("getMethod") == m.getName ||
      attr.getDescriptor.getFieldValue("setMethod") == m.getName
    }
    c.getMethods.collect {
      case m if isOperation(m) || isAttributeMethod(m) => createOperationInfo(m) 
    }
  }
  
  private def createOperationInfo(method: Method): ModelMBeanOperationInfo = {
    val managed = method.getAnnotation(classOf[Managed])
    def description = if (managed == null) method.getName else managed.description
    val desc = new DescriptorSupport 
    desc.setField("name", method.getName)
    desc.setField("descriptorType", "operation")
    desc.setField("role", "operation")
    for (timeLimit <- translateTimeLimit(managed))
      desc.setField("currencyTimeLimit", timeLimit)
    new ModelMBeanOperationInfo(description, method, desc)
  }
  
  private def translateTimeLimit(m: Managed) = {
    if (m eq null) None
    else CurrencyTimeLimitTranslator.translate(m.currencyTimeLimit)
  }
}


private object CurrencyTimeLimitTranslator {  
  
  private val decade = 315360000
  
  def translate(seconds: Int) = seconds match {
    case _ if seconds < 0 => None
    case 0                => Some(decade)
    case _                => Some(seconds)
  }   
}
