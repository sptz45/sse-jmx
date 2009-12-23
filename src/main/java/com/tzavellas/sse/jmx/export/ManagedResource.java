package com.tzavellas.sse.jmx.export;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ManagedResource {
  
  /**
   * The ObjectName to use for registering the managed resource.
   */
  String objectName() default "";
  
  /**
   * The description of the MBean.
   */
  String description() default "";
}
