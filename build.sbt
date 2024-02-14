import Settings._

version := "0.1.0-SNAPSHOT"

name := "tcssim"

scalaVersion := "3.4.0"

lazy val epics = project
  .in(file("./modules/epics"))
  .settings(
    name                     := "epics",
    libraryDependencies ++= Seq(
      Cats.value,
      CatsEffect.value,
      EpicsJCA.value,
      Log4Cats.value,
      Fs2.value,
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
      Fs2.value,
      Mouse.value
    ) ++ LucumaCore.value ++ MUnit.value ++ Logging.value,
    Test / parallelExecution := false,
    reStart / mainClass      := Some("tcssim.TcsSimApp")
  )
  .dependsOn(epics)
