package com.tzavellas.sse.jmx.export;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Managed {
	
  /**
   * The description of the attribute or operation.
   */
	String description() default "";
	
	/**
	 * If the attribute is read-only.
	 * 
	 * <p>By default if the attribute is a <i>var</i> it supports writing.</p>
	 */
	boolean readOnly() default false;
	
	/**
	 * How long (in seconds) the value of the attribute or operation is valid.
	 * 
	 * <p>If the value never expires set <code>currencyTimeLimit</code> to <i>zero</i>,
	 * if it always expires set it to a <i>negative value</i>, else set it to the number
	 * of <i>seconds</i> the values is valid.</p>

	 * <p>Because of inconsistencies in the JMX specification the JDK documentation recommends
	 * that you should not to use negative or zero values for currencyTimeLimit. This does not
	 * apply for this implementation since we translate your input values to values recommended
	 * by the JDK documentation automatically.</p>  
	 */
	int currencyTimeLimit() default -1;
}
