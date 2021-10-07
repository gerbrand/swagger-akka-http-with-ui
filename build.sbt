import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.swagger-akka-http"

val akkaVersion = "2.6.16"
val jacksonVersion = "2.13.0"
val swaggerVersion = "2.1.11"

val scala213 = "2.13.6"

ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := Seq(scala213, "2.12.15")

update / checksums := Nil

lazy val root = (project in file("."))
  .settings(
    name := "swagger-akka-http-with-ui",
    libraryDependencies ++= Seq("com.github.swagger-akka-http" %% "swagger-akka-http" % "2.6.0",
      "org.webjars" % "webjars-locator" % "0.41",
      "org.webjars" % "swagger-ui" % "3.50.0"),
  )


Test / testOptions += Tests.Argument("-oD")

Test / publishArtifact := false

Test / parallelExecution := false

pomIncludeRepository := { _ => false }

homepage := Some(url("https://github.com/swagger-akka-http/swagger-akka-http"))

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

pomExtra := (
  <developers>
    <developer>
      <id>gerbrand</id>
      <name>Gerbrand van Dieijen</name>
      <url>https://software-creation.nl</url>
    </developer>
  </developers>)

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11", "adopt@1.16")
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")),
  RefPredicate.Equals(Ref.Branch("swagger-1.5")),
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
