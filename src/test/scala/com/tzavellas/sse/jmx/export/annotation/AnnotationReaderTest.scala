/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export.annotation

import org.junit.Test
import org.junit.Assert._

class AnnotationReaderTest {

  private val ar = AnnotationReader

  @Test
  def class_with_no_annotations(): Unit = {
    assertFalse(ar.hasManagedResourceAnnotation(classOf[HasNoAnnotations]))
    assertEquals(None, ar.getResourceDescription(classOf[HasNoAnnotations]))
    assertEquals(None, ar.getObjectName(classOf[HasNoAnnotations]))

    val m = classOf[HasNoAnnotations].getMethod("hashCode")
    assertFalse(ar.hasManagedAnnotation(m))
    assertEquals(None, ar.getDescription(m))
    assertEquals(None, ar.getCurrencyTimeLimit(m))
    assertFalse(ar.getReadOnly(m))
  }

  @Test
  def class_with_default_annotations(): Unit = {
    assertTrue(ar.hasManagedResourceAnnotation(classOf[HasDefaultAnnotations]))
    assertEquals(Some("description"), ar.getResourceDescription(classOf[HasDefaultAnnotations]))
    assertEquals(Some("object-name"), ar.getObjectName(classOf[HasDefaultAnnotations]))

    val m = classOf[HasDefaultAnnotations].getMethod("a")
    assertTrue(ar.hasManagedAnnotation(m))
    assertEquals(Some("description"), ar.getDescription(m))
    assertEquals(Some(10), ar.getCurrencyTimeLimit(m))
    assertTrue(ar.getReadOnly(m))
  }

  @Test
  def class_with_default_annotations_but_no_values(): Unit = {
    assertTrue(ar.hasManagedResourceAnnotation(classOf[HasDefaultAnnotationsWithNoValues]))
    assertEquals(None, ar.getResourceDescription(classOf[HasDefaultAnnotationsWithNoValues]))
    assertEquals(None, ar.getObjectName(classOf[HasDefaultAnnotationsWithNoValues]))

    val m = classOf[HasDefaultAnnotationsWithNoValues].getMethod("a")
    assertTrue(ar.hasManagedAnnotation(m))
    assertEquals(None, ar.getDescription(m))
    assertEquals(None, ar.getCurrencyTimeLimit(m))
    assertFalse(ar.getReadOnly(m))
  }

  @Test
  def class_with_custom_annotations(): Unit = {
    assertTrue(ar.hasManagedResourceAnnotation(classOf[HasCustomAnnotations]))
    assertEquals(Some("description"), ar.getResourceDescription(classOf[HasCustomAnnotations]))
    assertEquals(Some("object-name"), ar.getObjectName(classOf[HasCustomAnnotations]))

    val m = classOf[HasCustomAnnotations].getMethod("a")
    assertTrue(ar.hasManagedAnnotation(m))
    assertEquals(Some("description"), ar.getDescription(m))
    assertEquals(Some(10), ar.getCurrencyTimeLimit(m))
    assertTrue(ar.getReadOnly(m))
  }

  @Test
  def class_with_custom_annotations_but_no_values(): Unit = {
    assertTrue(ar.hasManagedResourceAnnotation(classOf[HasCustomAnnotationsWithNoValues]))
    assertEquals(None, ar.getResourceDescription(classOf[HasCustomAnnotationsWithNoValues]))
    assertEquals(None, ar.getObjectName(classOf[HasCustomAnnotationsWithNoValues]))

    val m = classOf[HasCustomAnnotationsWithNoValues].getMethod("a")
    assertTrue(ar.hasManagedAnnotation(m))
    assertEquals(None, ar.getDescription(m))
    assertEquals(None, ar.getCurrencyTimeLimit(m))
    assertFalse(ar.getReadOnly(m))
  }

  @Test
  def class_with_custom_annotations_but_no_properties(): Unit = {
    assertTrue(ar.hasManagedResourceAnnotation(classOf[HasEmptyAnnotations]))
    assertEquals(None, ar.getResourceDescription(classOf[HasEmptyAnnotations]))
    assertEquals(None, ar.getObjectName(classOf[HasEmptyAnnotations]))

    val m = classOf[HasEmptyAnnotations].getMethod("a")
    assertTrue(ar.hasManagedAnnotation(m))
    assertEquals(None, ar.getDescription(m))
    assertEquals(None, ar.getCurrencyTimeLimit(m))
    assertFalse(ar.getReadOnly(m))
  }


  // -- test classes ----------------------------------------------------------

  @ManagedResource(objectName="object-name", description="description")
  class HasDefaultAnnotations {
    @Managed(description="description", readOnly=true, currencyTimeLimit=10)
    def a = 1
  }

  @ManagedResource
  class HasDefaultAnnotationsWithNoValues {
    @Managed
    def a = 1
  }

  class HasNoAnnotations

  @MyManagedResource(objectName="object-name", description="description")
  class HasCustomAnnotations {
    @Managed(description="description", readOnly=true, currencyTimeLimit=10)
    def a = 1
  }

  @MyManagedResource
  class HasCustomAnnotationsWithNoValues {
    @Managed
    def a = 1
  }

  @EmptyManagedResource
  class HasEmptyAnnotations {
    @EmptyManaged
    def a = 1
  }
}
