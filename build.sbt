
name := "sse-jmx"
description := "JMX Library for Scala"
startYear := Some(2009)

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.2")
releaseCrossBuild := true

libraryDependencies ++= Seq(
  "junit"             % "junit"           % "4.12" % "test",
  "com.novocode"      % "junit-interface" % "0.11" % "test"
)

compileOrder := CompileOrder.JavaThenScala
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q")
parallelExecution in Test := false
