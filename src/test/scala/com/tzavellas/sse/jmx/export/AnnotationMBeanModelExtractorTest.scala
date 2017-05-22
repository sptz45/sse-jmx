/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._
import org.junit.Test
import org.junit.Assert._

class AnnotationMBeanModelExtractorTest {

  private val extractor  = new AnnotationMBeanModelExtractor
  private val attributes = extractor.attributes(classOf[ManagedBean])
  private val operations = extractor.operations(classOf[ManagedBean], attributes)

  @Test
  def can_extract_model_only_from_annotated_classes(): Unit = {
    assertFalse(extractor.canExtractModel(classOf[String]))
    assertTrue(extractor.canExtractModel(classOf[ClassWithManagedAttribute]))
    assertTrue(extractor.canExtractModel(classOf[ClassWithManagedOperation]))
  }

  @Test
  def methods_annotated_with_managed_are_mbean_operations(): Unit = {
    assertEquals("operation", operation("operation").getName)
  }

  @Test
  def specify_description_via_annotation(): Unit = {
    assertEquals("An operation", operation("operation").getDescription)
  }

  @Test
  def currency_time_limit_in_seconds(): Unit = {
    assertEquals(10, currencyTimeLimit(operation("expiresInTen")))
  }

  @Test
  def currency_time_limit_is_set_to_a_big_int_if_is_zero_aka_always_valid(): Unit = {
    assertTrue(currencyTimeLimit(operation("alwaysValid")) > 10000)
  }

  @Test
  def currency_time_limit_is_not_present_if_has_negative_value_aka_never_valid(): Unit = {
    assertNull(operation("expires").getDescriptor.getFieldValue("currencyTimeLimit"))
  }

  @Test
  def val_as_read_only_attribute(): Unit = {
    val ro = attribute("readOnlyVal")
    assertTrue(ro.isReadable)
    assertFalse(ro.isWritable)
  }

  @Test
  def var_as_read_only_attribute_via_annotation(): Unit = {
    val ro = attribute("readOnlyVar")
    assertTrue(ro.isReadable)
    assertFalse(ro.isWritable)
  }

  @Test
  def var_attribute_is_read_n_write(): Unit = {
    val rw = attribute("writable")
    assertTrue(rw.isReadable)
    assertTrue(rw.isWritable)
  }

  @Test
  def currency_time_limit_in_attributes(): Unit = {
    assertTrue(attribute("alwaysValidVar").getDescriptor.getFieldValue("currencyTimeLimit").asInstanceOf[Int] > 10000)
  }

  @Test
  def vars_in_constructors_can_be_managed(): Unit = {
    assertFalse(extractor.attributes(classOf[VarInConstructor]).isEmpty)
  }

  // -- Test helpers ----------------------------------------------------------

  private def attribute(name: String) = attributes.find(_.getName == name).get

  private def operation(name: String) = operations.find(_.getName == name).get

  private def currencyTimeLimit(info: ModelMBeanOperationInfo) =
    info.getDescriptor.getFieldValue("currencyTimeLimit").asInstanceOf[Int]

  // -- Test classes ----------------------------------------------------------

  class ManagedBean {

    @Managed(description="An operation")
    def operation(): Unit = { }

    @Managed(currencyTimeLimit = 0)
    def alwaysValid = "valid"

    @Managed(currencyTimeLimit = -1)
    def expires = "expires"

    @Managed(currencyTimeLimit = 10)
    def expiresInTen = "ten"

    @Managed
    val readOnlyVal = 12

    @Managed(readOnly = true)
    var readOnlyVar = 13

    @Managed
    var writable = 14

    @Managed(currencyTimeLimit = 0)
    var alwaysValidVar = 10
  }

  class VarInConstructor(@Managed var attribute: String)

  class ClassWithManagedAttribute {
    @Managed
    var attr = 1
  }

  class ClassWithManagedOperation {
    @Managed
    def op(): Unit = { }
  }
}
