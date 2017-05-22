/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._
import javax.management.ObjectName

class ObjectNamingStrategiesTest {

  import ObjectNamingStrategies._
  
  @Test
  def the_name_has_the_package_as_domain_and_the_class_name_as_type_property(): Unit = {
    assertEquals(new ObjectName("java.lang:type=String"), useFullClassName(classOf[String]))
  }
  
  @Test
  def use_simple_class_name_and_specified_domain(): Unit = {
    val naming = useSimpleClassName("my-domain")
    assertEquals(new ObjectName("my-domain:type=String"), naming(classOf[String]))
  }
  
  @Test
  def cannot_create_name_for_class_without_the_managedResource_annotation(): Unit = {
    assertFalse(useAnnotation.isDefinedAt(classOf[String]))
  }
  
  @Test
  def create_name_using_the_managedResource_annotation(): Unit = {
    assertEquals(new ObjectName("com.tzavellas:type=annotated"), useAnnotation(classOf[AnnotatedClass]))
  }
  
  @Test
  def cannot_create_name_if_annotation_has_no_value(): Unit = {
    assertFalse(useAnnotation.isDefinedAt(classOf[NoObjectNameSpecified]))
  }
  
   @Test
  def default_naming_should_use_the_object_name_in_annotation(): Unit = {
    assertEquals(new ObjectName("com.tzavellas:type=annotated"), default(classOf[AnnotatedClass]))
  }
  
  @Test
  def default_naming_should_use_default_strategy_if_annotation_has_empty_value(): Unit = {
    assertEquals(new ObjectName("com.tzavellas.sse.jmx.export:type=NoObjectNameSpecified"), default(classOf[NoObjectNameSpecified]))
  }
  
  @Test
  def default_should_use_default_strategy_if_no_annotation_present(): Unit = {
    assertEquals(new ObjectName("java.lang:type=String"), default(classOf[String]))
  }

  // -- test classes ----------------------------------------------------------
  
  @ManagedResource(objectName="com.tzavellas:type=annotated")
  class AnnotatedClass
  
  @ManagedResource
  class NoObjectNameSpecified
}
