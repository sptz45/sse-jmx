/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._ 
import org.junit.Test
import org.junit.Assert._

class AnnotationMBeanInfoAssemblerTest {
  
  def assembler  = AnnotationMBeanInfoAssembler
  val attributes = assembler.attributes(classOf[ManagedBean])
  val operations = assembler.operations(classOf[ManagedBean], attributes)

  @Test
  def methods_annotated_with_managed_are_mbean_operations() {
    assertEquals("operation", operation("operation").getName)
  }
  
  @Test
  def specify_description_via_annotation() {
    assertEquals("An operation", operation("operation").getDescription)
  }
  
  @Test
  def currency_time_limit_in_seconds() {
    assertEquals(10, currencyTimeLimit(operation("expiresInTen")))
  }
  
  @Test
  def currency_time_limit_is_set_to_a_big_int_if_is_zero_aka_always_valid() {
    assertTrue(currencyTimeLimit(operation("alwaysValid")) > 10000)
  }
  
  @Test
  def currency_time_limit_is_not_present_if_has_negative_value_aka_never_valid() {
    assertNull(operation("expires").getDescriptor.getFieldValue("currencyTimeLimit"))
  }
  
  @Test
  def val_as_read_only_attribute() {
    val ro = attribute("readOnlyVal")
    assertTrue(ro.isReadable)
    assertFalse(ro.isWritable)
  }
  
  @Test
  def var_as_read_only_attribute_via_annotation() {
    val ro = attribute("readOnlyVar")
    assertTrue(ro.isReadable)
    assertFalse(ro.isWritable)
  }
  
  @Test
  def var_attribute_is_read_n_write() {
    val rw = attribute("writable")
    assertTrue(rw.isReadable)
    assertTrue(rw.isWritable)
  }
  
  @Test
  def currency_time_limit_in_attributes() {
    assertTrue(attribute("alwaysValidVar").getDescriptor.getFieldValue("currencyTimeLimit").asInstanceOf[Int] > 10000)
  }
  
  @Test
  def vars_in_constructors_can_be_managed() {
    assertFalse(assembler.attributes(classOf[VarInConstructor]).isEmpty)
  }
  
  // -- Test helpers ----------------------------------------------------------
  
  def attribute(name: String) = attributes.find(_.getName == name).get
  
  def operation(name: String) = operations.find(_.getName == name).get
  
  def currencyTimeLimit(info: ModelMBeanOperationInfo) =
    info.getDescriptor.getFieldValue("currencyTimeLimit").asInstanceOf[Int]

  // -- Test classes ----------------------------------------------------------
  
  class ManagedBean {

    @Managed(description="An operation")
    def operation() { }

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
}