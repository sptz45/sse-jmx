
# sse-jmx

The aim of this library is to make working with *JMX* in *Scala* simpler.

## Usage

### Exporting your Scala objects to JMX

You can export a any Scala object as `ModelMBean` by using the `MBeanExporter`
class. The `MBeanExporter` uses a configured `MBeanInfoAssembler` (by default
`AnnotationMBeanInfoAssembler` to create an MBean model from the object's
class.  

	class Cache {
	  
	  @Managed
	  var size: Int
	
	  @Mavaged
	  def evict(key: String) { /* ... */ }
	  
	  // the cache implementation...
	}

	val myCache = new Cache
	val exporter = new MBeanExporter
	exporter.export(myCache)
	

## License

Licensed under the Apache License, Version 2.0. See the LICENSE and NOTICE
files for more information.