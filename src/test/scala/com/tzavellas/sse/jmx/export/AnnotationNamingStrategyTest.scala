/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._

class AnnotationNamingStrategyTest {
  import AnnotationNamingStrategyTest._
  
  private def naming = AnnotationNamingStrategy

  @Test
  def cannot_create_name_for_class_without_the_managedResource_annotation() {
    assertFalse(naming.canCreateNameFor(classOf[String]))
  }
  
  @Test
  def create_name_using_the_managedResource_annotation() {
    assertTrue(naming.canCreateNameFor(classOf[AnnotatedClass]))
    assertEquals("com.tzavellas:type=annotated", naming.nameFor(classOf[AnnotatedClass]).toString)
  }
  
  @Test
  def cannot_create_name_if_annotation_has_no_value() {
    assertFalse(naming.canCreateNameFor(classOf[NoObjectNameSpecified]))
  }
}


object AnnotationNamingStrategyTest {
  
  @ManagedResource(objectName="com.tzavellas:type=annotated")
  class AnnotatedClass
  
  @ManagedResource
  class NoObjectNameSpecified
}