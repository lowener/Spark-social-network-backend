name := "spark_project"

version := "0.1"

scalaVersion := "2.11.8"

//lazy val backend = (project in file("backend"))
//  .settings(settings)
//  .settings(project_settings)
//  .settings(
//    assemblySettings,
//    name := "sender"
//    //mainClass in Compile := Some("Sender")
//  )

//lazy val root = (project in file(".")).dependsOn(messenger).aggregate(messenger)
//resolvers ++= Seq(
//  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
//  "Confluent" at "http://packages.confluent.io/maven/",
//  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
//  Resolver.sonatypeRepo("releases"),
//  Resolver.sonatypeRepo("snapshots")
//)

//libraryDependencies ++= Seq(
//  "org.apache.spark" % "spark-core_2.11" % "2.1.0",
//  "joda-time" % "joda-time" % "2.9.9",
//  "com.google.cloud" % "google-cloud-pubsub" % "1.31.0"
//)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.5",
)

lazy val messenger = (project in file("messenger"))
  .settings(settings)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.spark" % "spark-core_2.11" % "2.3.0",
      "joda-time" % "joda-time" % "2.9.9",
      "com.google.cloud" % "google-cloud-pubsub" % "1.31.0"
    ),
    assemblySettings,
    name := "sender",
    mainClass in Compile := Some("socialMedia.messager.Sender")
  )


lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

lazy val settings =
  commonSettings ++
    wartremoverSettings ++
    scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Confluent" at "http://packages.confluent.io/maven/",
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )
