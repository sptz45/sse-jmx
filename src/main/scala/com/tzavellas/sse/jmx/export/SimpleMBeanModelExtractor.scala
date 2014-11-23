/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._
import java.lang.reflect.Method

/**
 * Creates `ModelMBeanInfo` from classes using a simple set of conventions.
 *
 * <p>This implementation will generate:</p>
 * <ul>
 * <li>an operation for every method that returns Unit</li>
 * <li>an attribute for every var</li>
 * <li>a read-only attribute for every method that takes no arguments and returns
 * a non-Unit value</li>
 * </ul>
 *
 * The methods: `toString`, `getClass`, `clone`, `hashCode`,
 * {@code wait}, `notify`, `notifyAll` and `equals` are not used
 * for the generation of the model MBean.
 */
class SimpleMBeanModelExtractor extends MBeanModelExtractor {

  val excludedAttributes = List("toString", "getClass", "clone", "hashCode")

  val excludedOperations = List("wait", "notify", "notifyAll", "equals")

  def canExtractModel(c: Class[_]) = true

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
     .filterNot(m => m.isSynthetic || m.isBridge)
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
