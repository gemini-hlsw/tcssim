import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Settings {

  object LibraryVersion {
    // Scala libraries
    lazy val catsEffect = "3.3.11"
    lazy val cats       = "2.9.0"
    lazy val mouse      = "1.0.7"
    lazy val fs2        = "3.2.4"

    lazy val munit           = "0.7.29"
    lazy val munitCatsEffect = "1.0.6"
    lazy val munitDiscipline = "1.0.9"

    // EPICS Libraries
    lazy val jca = "2.4.6"

    // Lucuma
    lazy val lucumaCore = "0.28.0"
  }

  val MUnit = Def.setting(
    Seq(
      "org.scalameta" %%% "munit"               % LibraryVersion.munit           % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % LibraryVersion.munitCatsEffect % Test,
      "org.typelevel" %%% "discipline-munit"    % LibraryVersion.munitDiscipline % Test
    )
  )

  val EpicsJCA = "org.epics" % "jca" % LibraryVersion.jca

  val Cats       = Def.setting("org.typelevel" %%% "cats-core" % LibraryVersion.cats)
  val CatsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % LibraryVersion.catsEffect)
  val Fs2        = "co.fs2" %% "fs2-core" % LibraryVersion.fs2

  // Lucuma libraries
  val LucumaCore = Def.setting(
    Seq(
      "edu.gemini" %%% "lucuma-core"         % LibraryVersion.lucumaCore,
      "edu.gemini" %%% "lucuma-core-testkit" % LibraryVersion.lucumaCore
    )
  )

}
