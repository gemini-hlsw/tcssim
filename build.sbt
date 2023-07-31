import Settings._

version := "0.1.0-SNAPSHOT"

name := "tcssim"

scalaVersion := "3.3.0"

lazy val epics = project
  .in(file("./modules/epics"))
  .settings(
    name                     := "epics",
    libraryDependencies ++= Seq(
      Cats.value,
      CatsEffect.value,
      EpicsJCA,
      Log4Cats.value,
      Fs2,
      Log4CatsNoop.value
    ) ++ LucumaCore.value ++ MUnit.value,
    Test / parallelExecution := false,
    test                     := {}
  )

lazy val tcssim = project
  .in(file("./modules/tcssim"))
  .settings(
    name                     := "tcssim",
    libraryDependencies ++= Seq(
      Cats.value,
      Kittens.value,
      CatsEffect.value,
      Fs2
    ) ++ LucumaCore.value ++ MUnit.value ++ Logging.value,
    Test / parallelExecution := false,
    reStart / mainClass      := Some("tcssim.TcsSimApp")
  )
  .dependsOn(epics)
