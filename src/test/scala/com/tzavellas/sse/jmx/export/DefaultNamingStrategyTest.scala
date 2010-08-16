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
    val naming = new DefaultNamingStrategy("my-domain")
    assertEquals(new ObjectName("my-domain:type=String"), naming.nameFor(classOf[String]))
  }
}
