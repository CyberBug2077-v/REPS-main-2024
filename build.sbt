ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "project 2"
  )

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "5.2.2"
libraryDependencies += "org.scalanlp" %% "breeze-viz" % "1.2"
libraryDependencies += "org.jfree" % "jfreechart" % "1.5.3"

