// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait GuiderTrackCommands[F[_]] {
  val pwfs1: CadRecord9[F]
  val pwfs2: CadRecord9[F]
  val oiwfs: CadRecord9[F]
  val g1: CadRecord9[F]
  val g2: CadRecord9[F]
  val g3: CadRecord9[F]
  val g4: CadRecord9[F]
}

object GuiderTrackCommands {
  val ConfigPrefix: String = "config"
  val Pwfs1Name: String    = "Pwfs1"
  val Pwfs2Name: String    = "Pwfs2"
  val OiwfsName: String    = "Oiwfs"
  val G1Name: String       = "G1"
  val G2Name: String       = "G2"
  val G3Name: String       = "G3"
  val G4Name: String       = "G4"

  final case class GuiderTrackCommandsImpl[F[_]] private (
    pwfs1: CadRecord9[F],
    pwfs2: CadRecord9[F],
    oiwfs: CadRecord9[F],
    g1:    CadRecord9[F],
    g2:    CadRecord9[F],
    g3:    CadRecord9[F],
    g4:    CadRecord9[F]
  ) extends GuiderTrackCommands[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, GuiderTrackCommands[F]] = for {
    p1 <- CadRecord9.build(server, top + ConfigPrefix + Pwfs1Name)
    p2 <- CadRecord9.build(server, top + ConfigPrefix + Pwfs2Name)
    oi <- CadRecord9.build(server, top + ConfigPrefix + OiwfsName)
    g1 <- CadRecord9.build(server, top + ConfigPrefix + G1Name)
    g2 <- CadRecord9.build(server, top + ConfigPrefix + G2Name)
    g3 <- CadRecord9.build(server, top + ConfigPrefix + G3Name)
    g4 <- CadRecord9.build(server, top + ConfigPrefix + G4Name)
  } yield GuiderTrackCommandsImpl(p1, p2, oi, g1, g2, g3, g4)

}
