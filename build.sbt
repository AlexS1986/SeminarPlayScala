name := """SeminarPlay"""
organization := "Fernuniversitaet Hagen"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  jdbc,
  evolutions, // Database
  ehcache,
  ws,
  guice,
  "com.softwaremill.macwire" %% "macros" % "2.3.6" % "provided",
  "org.postgresql" % "postgresql" % "42.1.1",
  "com.h2database"  %  "h2"                           % "1.4.200", // your jdbc driver here
  "org.scalikejdbc" %% "scalikejdbc"                  % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config"           % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5",
  "de.svenkubiak" % "jBCrypt" % "0.4.1"
)


//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "Fernuniversitaet Hagen.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "Fernuniversitaet Hagen.binders._"

// Compile the project before generating Eclipse files, so
// that generated .scala or .class files for views and routes are present

//EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)
