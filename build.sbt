ThisBuild / resolvers ++= Seq(
    "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
    Resolver.mavenLocal
)

name := "AppOnibusFlinkBackEnd"

version := "0.1-SNAPSHOT"

organization := "github.jlucartc"

ThisBuild / scalaVersion := "2.12.1"

val flinkVersion = "1.10.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.kafka" % "kafka-clients" % "2.3.0",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "org.slf4j" % "slf4j-simple" % "1.6.2" % Test,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" % "flink-core" % flinkVersion,
  "org.apache.flink" %% "flink-runtime" % flinkVersion % Test,
  "org.apache.flink" %% "flink-runtime-web" % flinkVersion % Test,
  "org.apache.flink" %% "flink-clients" % flinkVersion,
  "log4j" % "log4j" % "1.2.17",
  "org.apache.flink" %% "flink-cep" % flinkVersion,
  "org.apache.flink" %% "flink-jdbc" % flinkVersion,
  "org.postgresql" % "postgresql" % "42.2.9",
  "com.github.mauricio" %% "postgresql-async" % "0.2.21",
  "org.apache.flink" % "flink-table-common" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-api-scala" % flinkVersion,
  "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion,
  "org.apache.flink" %% "flink-table-planner-blink" % flinkVersion,
   "org.apache.flink" %% "flink-table-runtime-blink" % flinkVersion,
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.2",
  "com.github.mauricio" %% "postgresql-async" % "0.2.21",
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

assembly / mainClass := Some("github.jlucartc.Main")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
                                   Compile / run / mainClass,
                                   Compile / run / runner
                                  ).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
