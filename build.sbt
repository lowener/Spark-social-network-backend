name := "spark_project"

version := "0.1"

scalaVersion := "2.11.11"
//scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.5",
  "org.apache.spark" % "spark-core_2.11" % "2.3.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.3.0",
  "org.apache.spark" % "spark-mllib_2.11" % "2.3.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.3.0",
  "joda-time" % "joda-time" % "2.9.9",
  "com.google.cloud" % "google-cloud-pubsub" % "1.31.0",
  "com.google.apis" % "google-api-services-pubsub" % "v1-rev355-1.22.0",
  "org.apache.bahir" % "spark-streaming-pubsub_2.11" % "2.2.1"
)
resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Confluent" at "http://packages.confluent.io/maven/",
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)
mainClass in run := Some("socialMedia.backend.SentimentAnalysis")

//lazy val mess = (project in file("produc"))
//  .settings(settings)
//  .settings(
//    libraryDependencies ++= Seq(
//      "org.apache.spark" % "spark-core_2.11" % "2.3.0",
//      "org.apache.spark" % "spark-streaming_2.11" % "2.3.0",
//      "joda-time" % "joda-time" % "2.9.9",
//      "com.google.cloud" % "google-cloud-pubsub" % "1.31.0",
//      "com.google.apis" % "google-api-services-pubsub" % "v1-rev355-1.22.0",
//      "org.apache.bahir" % "spark-streaming-pubsub_2.11" % "2.1.1"
//      //"com.google.cloud.bigdataoss" % "util" % "1.6.0",
//      //"org.apache.spark" % "spark-tags_2.11" % "2.3.0"
//    ),
//    assemblySettings,
//    name := "product",
//    mainClass in assembly:= Some("socialMedia.messager.MyProducer")
//  )
//
//
//lazy val assemblySettings = Seq(
//  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = true),
//  assemblyJarName in assembly := name.value + ".jar",
//  assemblyMergeStrategy in assembly := {
//    case _                             => MergeStrategy.rename
//    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//    case _                             => MergeStrategy.first
//  }
//)
//
//lazy val settings =
//  commonSettings ++
//    wartremoverSettings ++
//    scalafmtSettings
//
//lazy val compilerOptions = Seq(
//  "-unchecked",
//  "-feature",
//  "-language:existentials",
//  "-language:higherKinds",
//  "-language:implicitConversions",
//  "-language:postfixOps",
//  "-deprecation",
//  "-encoding",
//  "utf8"
//)
//
//lazy val commonSettings = Seq(
//  scalacOptions ++= compilerOptions,
//  resolvers ++= Seq(
//    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
//    "Confluent" at "http://packages.confluent.io/maven/",
//    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
//    Resolver.sonatypeRepo("releases"),
//    Resolver.sonatypeRepo("snapshots"),
//    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
//  )
//)
//
//lazy val wartremoverSettings = Seq(
//  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
//)
//
//lazy val scalafmtSettings =
//  Seq(
//    scalafmtOnCompile := true,
//    scalafmtTestOnCompile := true,
//    scalafmtVersion := "1.2.0"
//  )
//libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
