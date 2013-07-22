import AssemblyKeys._ // put this at the top of the file

assemblySettings

sbtVersion := "0.12.3"

name := "scala_excel"

organization := "binarytemple"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers += "Spray Repository" at "http://repo.spray.io/"

autoCompilerPlugins := true

libraryDependencies += "org.parboiled" % "parboiled-scala_2.10" % "1.1.5" 

libraryDependencies += "org.specs2" %% "specs2" % "1.13" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.0" % "test"

libraryDependencies += "jline" % "jline" % "2.10"

EclipseKeys.withSource := true

net.virtualvoid.sbt.graph.Plugin.graphSettings
