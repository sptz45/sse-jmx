package com.tzavellas.sse.jmx.export

import org.junit.Test
import org.junit.Assert._
import javax.management.{ObjectName, MalformedObjectNameException}

class DefaultNamingStrategyTest {

  val naming = new DefaultNamingStrategy
  
  @Test
  def by_default_the_name_has_the_package_as_domain_and_the_class_name_as_type_property() {
    assertEquals(new ObjectName("java.lang:type=String"), naming.nameFor(classOf[String]))
  }
  
  @Test
  def a_custom_domain_can_be_specified() {
    val naming = new DefaultNamingStrategy(Some("my-domain"))
    assertEquals(new ObjectName("my-domain:type=String"), naming.nameFor(classOf[String]))
  }
  
  @Test
  def object_name_contains_the_specified_key_properties() {
    assertEquals(
      new ObjectName("java.lang:type=String,email=true,required=false"),
      naming.nameFor(classOf[String], Map("email" -> true, "required" -> false)))
  }
  
  @Test
  def specified_custom_type_via_keys_replaces_class_name_as_type() {
    assertEquals(
      new ObjectName("java.lang:type=MyString"),
      naming.nameFor(classOf[String], Map("type" -> "MyString")))
  }
  
  @Test(expected=classOf[MalformedObjectNameException])
  def invalid_object_name_throws_exception() {
    naming.nameFor(classOf[String], Map("invalid-contains-colon:" -> 3))
  }
}
