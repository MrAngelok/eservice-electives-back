name := "eservice-electives"

version := "1.0"

scalaVersion := "2.11.11"

val scalikejdbcV = "2.2.5"

net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcV,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcV,
  "com.itextpdf" % "io" % "7.0.4",
  "com.itextpdf" % "kernel" % "7.0.4",
  "com.itextpdf" % "layout" % "7.0.4",
  "org.json4s" % "json4s-jackson_2.11" % "3.5.2",
  "org.scalatest" % "scalatest_2.11" % "3.0.0",
  "org.slf4j" % "slf4j-api" % "1.7.12" % "provided",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.12" exclude("org.slf4j", "slf4j-api"),
  "ch.qos.logback" % "logback-classic" % "1.1.3" exclude("org.slf4j", "slf4j-api"),
  "oracle" % "oracle-jdbc-connector" % "10.2.0.5.0",
  "com.zaxxer" % "HikariCP" % "2.6.1",
  "com.sparkjava" % "spark-core" % "2.7.1",
  "org.pac4j" % "spark-pac4j" % "1.2.0",
  "org.pac4j" % "pac4j-oauth" % "1.9.4",
  "com.ning" % "async-http-client" % "1.9.40"
)
