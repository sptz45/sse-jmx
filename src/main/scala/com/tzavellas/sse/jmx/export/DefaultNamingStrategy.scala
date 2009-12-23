package com.tzavellas.sse.jmx.export

import javax.management.ObjectName

/**
 * The default implementation of <code>ObjectNamingStrategy</code>.
 * 
 * <p>The format of the produced <code>ObjectName</code> is: <i>domain:type=type-value,key1=value1,...</i>,
 * where <i>domain</i> is the specified <code>domain</code> and <i>type-value</i> is the short
 * name of the specified class or a value specified in the key properties using the <code>type</code>
 * key.</p>
 * 
 * @param domain the domain that will be used for constructing the <code>ObjectNames</code>.
 *               If <code>None</code> then the domain used will be the package name of each
 *               specified class. The default value is <code>None</code>.
 * 
 * @see ObjectNamingStrategy
 */
class DefaultNamingStrategy(domain: Option[String] = None) extends ObjectNamingStrategy {
  
  def canCreateNameFor(clazz: Class[_]) = true
  
  def nameFor(clazz: Class[_], keyProperties: Map[String, Any]) = {
    val tp = keyProperties.getOrElse("type", clazz.getSimpleName)
    val dn = domain.getOrElse(clazz.getPackage.getName)
    
    val keysBuffer = new StringBuilder(",")
    
    for ((key, value) <- keyProperties if key != "type")
      keysBuffer.append(key).append("=").append(value).append(",")
    
    // remove the trailing ","
    keysBuffer.deleteCharAt(keysBuffer.length -1)
    
    new ObjectName(dn + ":type=" + tp + keysBuffer.toString)
  }
}
