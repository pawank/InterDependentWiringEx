name := "IRS"

version := "1.0"

scalaVersion := "2.9.1"

// increase the time between polling for file changes when using continuous execution
pollInterval := 1000

//seq(netbeans.NetbeansTasks.netbeansSettings:_*)
seq(webSettings :_*)

resolvers += ScalaToolsSnapshots

libraryDependencies ++= Seq(
	"net.liftweb" %% "lift-webkit" % "2.4-M4" % "compile" withSources (),
    "net.liftweb" %% "lift-mapper" % "2.4-M4" % "compile" withSources (),
    "net.liftweb" %% "lift-common" % "2.4-M4" % "compile" withSources (),
 "net.liftweb" %% "lift-mongodb" % "2.4-M4" withSources (),
   "net.liftweb" %% "lift-mongodb-record" % "2.4-M4" withSources (),
   "net.liftweb" %% "lift-facebook" % "2.4-M4",
   "net.liftweb" %% "lift-widgets" % "2.4-M4" withSources (),
   "net.liftweb" %% "lift-wizard" % "2.4-M4",
   "net.liftweb" %% "lift-oauth" % "2.4-M4",
   "net.liftweb" %% "lift-json" % "2.4-M4" withSources (),
    "org.scala-lang" % "scala-compiler" % "2.9.1" withSources (),
        "com.github.philcali" % "scalendar_2.9.0-1" % "0.0.5" % "compile",
        "org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "jetty",
        "org.eclipse.jetty" % "jetty-server" % "7.3.0.v20110203",
        "ch.qos.logback" % "logback-core" % "0.9.29" % "compile",
        "ch.qos.logback" % "logback-classic" % "0.9.29" % "compile",
        "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5" withSources (),
        "mysql" % "mysql-connector-java" % "5.1.12",
        "javax.mail" % "mail" % "1.4.1" withSources (),
        "org.apache.poi" % "poi" % "3.7" withSources (),
        "org.slf4j" % "log4j-over-slf4j" % "1.6.1",
    "junit" % "junit" % "4.5" % "test",
         "de.element34" % "sbt-eclipsify_2.8.1" % "0.10.0-SNAPSHOT",
    "org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
        "org.mortbay.jetty" % "jetty" % "6.1.25" % "test->default"
)
