import sbtrelease._
import ReleaseStateTransformations._

name := "sse-jmx"
organization := "com.tzavellas"

description := "JMX Library for Scala"
homepage := Some(url("http://www.github.com/sptz45/sse-jmx"))
startYear := Some(2009)
organizationName := "spiros.blog()"
organizationHomepage := Some(url("http://www.tzavellas.com"))
licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scmInfo := Some(
  ScmInfo(
    url("http://github.com/sptz45/sse-jmx/"),
    "scm:git:git://github.com/sptz45/sse-jmx.git",
    Some("scm:git:git@github.com:sptz45/sse-jmx.git")
  )
)

crossScalaVersions := Seq("2.10.4", "2.11.0", "2.12.0")
scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "junit"             % "junit"           % "4.12" % "test",
  "com.novocode"      % "junit-interface" % "0.11" % "test"
)

scalacOptions ++= List("-feature", "-unchecked", "-deprecation", "-encoding", "UTF-8")
compileOrder := CompileOrder.JavaThenScala
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q")
parallelExecution in Test := false


jacoco.settings

publishMavenStyle := true
publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <issueManagement>
    <system>GitHub</system>
    <url>http://github.com/sptz45/sse-jmx/issues</url>
  </issueManagement>
  <developers>
    <developer>
      <id>sptz45</id>
      <name>Spiros Tzavellas</name>
      <email>spiros at tzavellas dot com</email>
      <url>http://www.tzavellas.com</url>
      <timezone>+2</timezone>
      <roles>
        <role>developer</role>
      </roles>
    </developer>
  </developers>
)

releaseSettings

ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
ReleaseKeys.crossBuild := true

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion
)
