/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export.annotation;

import java.lang.annotation.*;

/**
 * Mark a Scala element as a JMX attribute or operation.
 * 
 * <p>Annotated <strong>def</strong>s become <em>operations</em>, annotated
 * <strong>var</strong>s become <em>attributes</em> and annotated
 * <strong>val</strong>s become <em>read-only attributes</em>.</p>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Managed {
	
  /** The description of the attribute or operation. */
	String description() default "";
	
	/**
	 * If the attribute is read-only.
	 * 
	 * <p>By default if the attribute is a <em>var</em> it supports writing.</p>
	 */
	boolean readOnly() default false;
	
	/**
	 * How long (in seconds) the value of the attribute or operation is valid.
	 * 
	 * <p>If the value never expires set <code>currencyTimeLimit</code> to
	 * <em>zero</em>, if it always expires set it to a <em>negative value</em>,
	 * else set it to the number of <em>seconds</em> the values is valid.</p>
	 * 
	 * <p>Because of inconsistencies in the JMX specification the JDK documentation
	 * recommends that you should not to use negative or zero values for
	 * currencyTimeLimit. This does not apply for this implementation since we
	 * translate your input values to values recommended by the JDK documentation
	 * automatically.</p>  
	 */
	int currencyTimeLimit() default -1;
}
