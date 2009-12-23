package com.tzavellas.sse.jmx.export

trait PlatformMBeanServer {
  val server = java.lang.management.ManagementFactory.getPlatformMBeanServer
}
