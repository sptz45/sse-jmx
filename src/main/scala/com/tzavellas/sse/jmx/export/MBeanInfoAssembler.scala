/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._

/**
 * Generates a `ModelMBeanInfo` from a class.
 */
class MBeanInfoAssembler(extractors: MBeanModelExtractor*) {

  def createMBeanInfo(clazz: Class[_]): ModelMBeanInfo = {
    val extractor = getExtracorForClass(clazz)
    assembleMBeanInfo(extractor, clazz)
  }

  private def getExtracorForClass(clazz: Class[_]) = {
    extractors.find(_.canExtractModel(clazz))
      .getOrElse(sys.error(s"Could not assemble a ModelMbeanInfo for class $clazz using ${extractors.map(_.getClass).mkString}"))
  }

  private def assembleMBeanInfo(extractor: MBeanModelExtractor, clazz: Class[_]): ModelMBeanInfo = {
    val attrs = extractor.attributes(clazz)
    new ModelMBeanInfoSupport(
      clazz.getName,
      extractor.description(clazz),
      attrs,
      extractor.constructors(clazz),
      extractor.operations(clazz, attrs),
      extractor.notifications(clazz))
  }
}

object MBeanInfoAssembler {
  val default = new MBeanInfoAssembler(new AnnotationMBeanModelExtractor, new SimpleMBeanModelExtractor)
}
