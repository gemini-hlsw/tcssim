// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait WavelengthCmds[F[_]] {
  val mount: CadRecord1[F]
  val sourceA: CadRecord1[F]
  val sourceB: CadRecord1[F]
  val sourceC: CadRecord1[F]
  val pwfs1: CadRecord1[F]
  val pwfs2: CadRecord1[F]
  val oiwfs: CadRecord1[F]
  val g1: CadRecord1[F]
  val g2: CadRecord1[F]
  val g3: CadRecord1[F]
  val g4: CadRecord1[F]
}

object WavelengthCmds {
  val WavelengthPrefix: String = "wavel"
  val MountName: String        = "Mount"
  val SourceAName: String      = "SourceA"
  val SourceBName: String      = "SourceB"
  val SourceCName: String      = "SourceC"
  val Pwfs1Name: String        = "Pwfs1"
  val Pwfs2Name: String        = "Pwfs2"
  val OiwfsName: String        = "Oiwfs"
  val G1Name: String           = "G1"
  val G2Name: String           = "G2"
  val G3Name: String           = "G3"
  val G4Name: String           = "G4"

  private case class WavelengthCmdsImpl[F[_]](
    mount:   CadRecord1[F],
    sourceA: CadRecord1[F],
    sourceB: CadRecord1[F],
    sourceC: CadRecord1[F],
    pwfs1:   CadRecord1[F],
    pwfs2:   CadRecord1[F],
    oiwfs:   CadRecord1[F],
    g1:      CadRecord1[F],
    g2:      CadRecord1[F],
    g3:      CadRecord1[F],
    g4:      CadRecord1[F]
  ) extends WavelengthCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, WavelengthCmds[F]] = for {
    mount   <- CadRecord1.build(server, top + WavelengthPrefix + MountName)
    sourcea <- CadRecord1.build(server, top + WavelengthPrefix + SourceAName)
    sourceb <- CadRecord1.build(server, top + WavelengthPrefix + SourceBName)
    sourcec <- CadRecord1.build(server, top + WavelengthPrefix + SourceCName)
    pwfs1   <- CadRecord1.build(server, top + WavelengthPrefix + Pwfs1Name)
    pwfs2   <- CadRecord1.build(server, top + WavelengthPrefix + Pwfs2Name)
    oiwfs   <- CadRecord1.build(server, top + WavelengthPrefix + OiwfsName)
    g1      <- CadRecord1.build(server, top + WavelengthPrefix + G1Name)
    g2      <- CadRecord1.build(server, top + WavelengthPrefix + G2Name)
    g3      <- CadRecord1.build(server, top + WavelengthPrefix + G3Name)
    g4      <- CadRecord1.build(server, top + WavelengthPrefix + G4Name)
  } yield WavelengthCmdsImpl(mount, sourcea, sourceb, sourcec, pwfs1, pwfs2, oiwfs, g1, g2, g3, g4)

}
