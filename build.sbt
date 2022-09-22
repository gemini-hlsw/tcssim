import Settings._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"

lazy val epics = project
  .in(file("./modules/epics"))
  .settings(
    name                     := "epics",
    libraryDependencies ++= Seq(
      Cats.value,
      CatsEffect.value,
      EpicsJCA,
      Fs2
    ) ++ LucumaCore.value ++ MUnit.value,
    Test / parallelExecution := false
  )

lazy val tcssim = project
  .in(file("./modules/tcssim"))
  .settings(
    name                     := "tcssim",
    libraryDependencies ++= Seq(
      Cats.value,
      CatsEffect.value,
      Fs2
    ) ++ LucumaCore.value ++ MUnit.value,
    Test / parallelExecution := false,
    reStart / mainClass      := Some("tcssim.TcsSimApp")
  )
  .dependsOn(epics)
