import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Settings {

  object LibraryVersion {
    // Scala libraries
    lazy val catsEffect = "3.6.1"
    lazy val cats       = "2.13.0"
    lazy val mouse      = "1.3.2"
    lazy val fs2        = "3.12.0"
    lazy val kittens    = "3.5.0"

    lazy val munit           = "1.1.1"
    lazy val munitCatsEffect = "2.1.0"
    lazy val munitDiscipline = "2.0.0"

    // EPICS Libraries
    lazy val jca = "2.4.10"

    // Lucuma
    lazy val lucumaCore = "0.210.0"

    val slf4j    = "2.0.17"
    val log4s    = "1.10.0"
    val log4cats = "2.7.0"
    val logback  = "1.5.18"
    val janino   = "3.1.12"
  }

  val MUnit = Def.setting(
    Seq(
      "org.scalameta" %%% "munit"             % LibraryVersion.munit           % Test,
      "org.typelevel" %%% "munit-cats-effect" % LibraryVersion.munitCatsEffect % Test,
      "org.typelevel" %%% "discipline-munit"  % LibraryVersion.munitDiscipline % Test
    )
  )

  val EpicsJCA = Def.setting("org.epics" % "jca" % LibraryVersion.jca)

  val Cats         = Def.setting("org.typelevel" %%% "cats-core" % LibraryVersion.cats)
  val Kittens      = Def.setting("org.typelevel" %%% "kittens" % LibraryVersion.kittens)
  val CatsEffect   = Def.setting("org.typelevel" %%% "cats-effect" % LibraryVersion.catsEffect)
  val Fs2          = Def.setting("co.fs2" %% "fs2-core" % LibraryVersion.fs2)
  val Mouse        = Def.setting("org.typelevel" %%% "mouse" % LibraryVersion.mouse)
  val Log4Cats     = Def.setting("org.typelevel" %%% "log4cats-slf4j" % LibraryVersion.log4cats)
  val Logback      = Def.setting(
    Seq(
      "ch.qos.logback"      % "logback-core"    % LibraryVersion.logback,
      "ch.qos.logback"      % "logback-classic" % LibraryVersion.logback,
      "org.codehaus.janino" % "janino"          % LibraryVersion.janino
    )
  )
  val Log4s        = Def.setting("org.log4s" %%% "log4s" % LibraryVersion.log4s)
  val JuliSlf4j    = Def.setting("org.slf4j" % "jul-to-slf4j" % LibraryVersion.slf4j)
  val Logging      = Def.setting(Seq(JuliSlf4j.value, Log4s.value) ++ Logback.value)
  val Log4CatsNoop =
    Def.setting("org.typelevel" %%% "log4cats-noop" % LibraryVersion.log4cats % "test")

  // Lucuma libraries
  val LucumaCore = Def.setting(
    Seq(
      "edu.gemini" %%% "lucuma-core"         % LibraryVersion.lucumaCore,
      "edu.gemini" %%% "lucuma-core-testkit" % LibraryVersion.lucumaCore
    )
  )

}
