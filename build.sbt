import sbt.Keys.libraryDependencies

organization := "com.github.swagger-akka-http"

name := "swagger-akka-http-with-ui"

val akkaVersion = "2.6.16"
val akkaHttpVersion = "10.2.6"
val jacksonVersion = "2.13.0"
val swaggerVersion = "2.1.11"

val scala213 = "2.13.6"
val slf4jVersion = "1.7.32"

ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := Seq(scala213, "2.12.15")

update / checksums := Nil

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq("com.github.swagger-akka-http" %% "swagger-akka-http" % "2.6.0",
      "org.webjars" % "webjars-locator" % "0.41",
      "org.webjars" % "swagger-ui" % "3.50.0",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.10" % Test,
      "org.json4s" %% "json4s-native" % "4.0.3" % Test,
      "org.jsoup" % "jsoup" % "1.14.3" % Test,
      "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.0.0" % Test,
      "joda-time" % "joda-time" % "2.10.12" % Test,
      "org.joda" % "joda-convert" % "2.2.1" % Test,
      "org.slf4j" % "slf4j-simple" % slf4jVersion % Test

    ),
  )


Test / testOptions += Tests.Argument("-oD")

Test / publishArtifact := false

Test / parallelExecution := false

pomIncludeRepository := { _ => false }

homepage := Some(url("https://github.com/gerbrand/swagger-akka-http-with-ui"))

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

pomExtra := (
  <developers>
    <developer>
      <id>gerbrand</id>
      <name>Gerbrand van Dieijen</name>
      <url>https://software-creation.nl</url>
    </developer>
  </developers>)

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11")
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)
