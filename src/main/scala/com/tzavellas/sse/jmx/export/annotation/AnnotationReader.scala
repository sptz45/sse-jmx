/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export.annotation

import java.lang.reflect.{AccessibleObject, AnnotatedElement}
import java.lang.annotation.Annotation


private[jmx] trait AnnotationReader {

  def hasManagedAnnotation(o: AccessibleObject): Boolean

  def getDescription(o: AccessibleObject): Option[String]

  def getReadOnly(o: AccessibleObject): Boolean

  def getCurrencyTimeLimit(o: AccessibleObject): Option[Int]

  def hasManagedResourceAnnotation(c: Class[_]): Boolean

  def getObjectName(c: Class[_]): Option[String]

  def getResourceDescription(c: Class[_]): Option[String]
}

object AnnotationReader extends AnnotationReader {

  def hasManagedAnnotation(o: AccessibleObject): Boolean =
    StaticAnnotationReader.hasManagedAnnotation(o) || CustomAnnotationReader.hasManagedAnnotation(o)

  def getDescription(o: AccessibleObject): Option[String] =
    StaticAnnotationReader.getDescription(o).orElse(CustomAnnotationReader.getDescription(o))

  def getReadOnly(o: AccessibleObject): Boolean =
    StaticAnnotationReader.getReadOnly(o) || CustomAnnotationReader.getReadOnly(o)

  def getCurrencyTimeLimit(o: AccessibleObject): Option[Int] = {
    StaticAnnotationReader.getCurrencyTimeLimit(o).orElse(CustomAnnotationReader.getCurrencyTimeLimit(o))
  }

  def hasManagedResourceAnnotation(c: Class[_]): Boolean =
    StaticAnnotationReader.hasManagedResourceAnnotation(c) || CustomAnnotationReader.hasManagedResourceAnnotation(c)

  def getObjectName(c: Class[_]): Option[String] =
    StaticAnnotationReader.getObjectName(c).orElse(CustomAnnotationReader.getObjectName(c))

  def getResourceDescription(c: Class[_]): Option[String] =
    StaticAnnotationReader.getResourceDescription(c).orElse(CustomAnnotationReader.getResourceDescription(c))
}

private abstract class BaseAnnotationReader extends AnnotationReader {
  protected def emptyStrToNone(s: String) = if (s == null || s == "") None else Option(s)
  protected def negativeIntToNone(i: Int) = if (i < 0) None else Some(i)
}

private object StaticAnnotationReader extends BaseAnnotationReader {

  def hasManagedAnnotation(o: AccessibleObject) = o.isAnnotationPresent(classOf[Managed])

  def getDescription(o: AccessibleObject) = {
    Option(o.getAnnotation(classOf[Managed])).map(_.description).flatMap(emptyStrToNone)
  }

  def getReadOnly(o: AccessibleObject) = {
    Option(o.getAnnotation(classOf[Managed])).map(_.readOnly).getOrElse(false)
  }

  def getCurrencyTimeLimit(o: AccessibleObject) = {
    Option(o.getAnnotation(classOf[Managed])).flatMap(a => negativeIntToNone(a.currencyTimeLimit))
  }

  def hasManagedResourceAnnotation(c: Class[_]) = c.isAnnotationPresent(classOf[ManagedResource])

  def getObjectName(c: Class[_]) = {
    Option(c.getAnnotation(classOf[ManagedResource])).map(_.objectName).flatMap(emptyStrToNone)
  }

  def getResourceDescription(c: Class[_]) =
    Option(c.getAnnotation(classOf[ManagedResource])).map(_.description).flatMap(emptyStrToNone)
}


private object CustomAnnotationReader extends BaseAnnotationReader {

  def hasManagedAnnotation(o: AccessibleObject) = getAnnotation(o).isDefined

  def getDescription(o: AccessibleObject) = {
    getAnnotation(o).flatMap(a => call[String](a, "description")).flatMap(emptyStrToNone)
  }

  def getReadOnly(o: AccessibleObject) = {
    getAnnotation(o).flatMap(a => call[Boolean](a, "readOnly")).getOrElse(false)
  }

  def getCurrencyTimeLimit(o: AccessibleObject) = {
    getAnnotation(o).flatMap(a => call[Int](a, "currencyTimeLimit")).flatMap(negativeIntToNone)
  }

  def hasManagedResourceAnnotation(c: Class[_]) = getAnnotation(c).isDefined

  def getObjectName(c: Class[_]) = {
    getAnnotation(c).flatMap(a => call[String](a, "objectName")).flatMap(emptyStrToNone)
  }

  def getResourceDescription(c: Class[_]) = {
    getAnnotation(c).flatMap(a => call[String](a, "description")).flatMap(emptyStrToNone)
  }

  private def getAnnotation(elem: AnnotatedElement) =
    elem.getAnnotations.find(_.annotationType.isAnnotationPresent(classOf[ManagedAnnotation]))

  private def call[T](a: Annotation, method: String): Option[T] = {
    a.annotationType.getMethods.find(_.getName == method).map(m => m.invoke(a).asInstanceOf[T])
  }
}
