# sse-jmx

[![Build Status](https://secure.travis-ci.org/sptz45/sse-jmx.png)](http://travis-ci.org/sptz45/sse-jmx)

The aim of this library is to make working with *JMX* in *Scala* simpler.

## Usage

### Exporting your Scala objects to JMX

You can export any Scala object as `ModelMBean` by using the `MBeanExporter`
class. The `MBeanExporter` uses a configured `MBeanInfoAssembler` (by default
`AnnotationMBeanInfoAssembler` to create an MBean model using reflection from
the object's class. The library ships with two implementations of the
`MBeanInfoAssembler` trait, `AnnotationMBeanInfoAssembler` and
`SimpleMBeanInfoAssembler`.

`SimpleMBeanInfoAssembler` will generate a model with an operation for every
method that takes no arguments and returns Unit, an attribute for every *var*
and a read-only attribute for every method that takes no arguments and returns
non-Unit value (usually a *val*).

`AnnotationMBeanInfoAssembler` maps *Scala* elements annotated with the `@Managed`
annotation to MBean operations and attributes. Annotated *defs* become operations,
annotated *vars* become attributes and annotated *vals* become read-only
attributes.

The `ObjectName` of an exported object is determined by a configured 
`ObjectNamingStratety` or it is specified explicitly. The name can be explicitly
specified when the object is registered or using the `@ManagedResource`
annotation.

In the below example code we have a `Cache` class that is annotated with the
`@Managed` annotation and exported using an `MBeanExporter` with the default
configuration. 

```scala
class Cache {

  @Managed(readOnly=true)
  var size: Int

  @Mavaged
  def evict(key: String) { /* ... */ }

  // the cache implementation...
}

class MyApplication {
  val myCache = new Cache
  val exporter = new MBeanExporter
  exporter.export(myCache)
}
```

## Maven

```xml
<dependency>
  <groupId>com.tzavellas</groupId>
  <artifactId>sse-jmx</artifactId>
  <version>0.4.2</version>
</dependency>
```


## License

Licensed under the Apache License, Version 2.0. See the LICENSE and NOTICE
files for more information.
