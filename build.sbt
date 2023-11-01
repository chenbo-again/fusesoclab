ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.16"
ThisBuild / organization := "org.example"

val spinalVersion = "1.8.0"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)

lazy val fusespinal = (project in file("."))
  .settings(
    Compile / scalaSource := baseDirectory.value / "hw" / "spinal",
    libraryDependencies ++= Seq(spinalCore, spinalLib, spinalIdslPlugin),
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.16.0-rc1",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.16.0-rc1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.16.0-rc1",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.16.0-rc1",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.16.0-rc1"
    ),
  )

fork := true
