package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * The default implementation of <code>ObjectNamingStrategy</code>.
 * 
 * <p>The format of the produced <code>ObjectName</code> is: <i>domain-value:type=type-value</i>,
 * where <i>domain-value</i> is the specified <code>domain</code> and <i>type-value</i> is the
 * simple name of the specified class.</p>
 * 
 * @param domain the domain that will be used for constructing the {@code ObjectName}
 *               instances. If {@code null} (the default) then the domain used will
 *               be the package name of each class.
 * 
 */
class DefaultNamingStrategy(domain: String = null) extends ObjectNamingStrategy {
  
  /** This implementation create {@code ObjectName} for all classes. */
  def canCreateNameFor(clazz: Class[_]) = true
  
  def nameFor(clazz: Class[_]) = {
    val domain = this.domain match {
      case null => clazz.getPackage.getName
      case _    => this.domain
    }
    new ObjectName(domain + ":type=" + clazz.getSimpleName)
  }
}
