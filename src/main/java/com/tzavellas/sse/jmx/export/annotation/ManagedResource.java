/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export.annotation;

import java.lang.annotation.*;

/**
 * Marks that a class should be exported to JMX.
 * 
 * <p>The usage of this annotation is optional and the only benefit from using
 * it is the ability to specify a custom <em>ObjectName</em> and <em>description</em>
 * for the generated MBean.</p> 
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ManagedResource {
  
  /** The ObjectName to use for registering the managed resource. */
  String objectName() default "";
  
  /** The description of the MBean. */
  String description() default "";
}
