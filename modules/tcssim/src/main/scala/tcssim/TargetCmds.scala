// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait TargetCmds[F[_]] {
  val sourceA: CadRecord12[F]
  val sourceB: CadRecord12[F]
  val sourceC: CadRecord12[F]
  val pwfs1: CadRecord12[F]
  val pwfs2: CadRecord12[F]
  val oiwfs: CadRecord12[F]
  val g1: CadRecord12[F]
  val g2: CadRecord12[F]
  val g3: CadRecord12[F]
  val g4: CadRecord12[F]
}

object TargetCmds {
  val SourceASuffix: String = "sourceA"
  val SourceBSuffix: String = "sourceB"
  val SourceCSuffix: String = "sourceC"
  val Pwfs1Suffix: String   = "pwfs1"
  val Pwfs2Suffix: String   = "pwfs2"
  val OiwfsSuffix: String   = "oiwfs"
  val G1Suffix: String      = "g1"
  val G2Suffix: String      = "g2"
  val G3Suffix: String      = "g3"
  val G4Suffix: String      = "g4"

  final case class TargetCmdsImpl[F[_]] private (
    sourceA: CadRecord12[F],
    sourceB: CadRecord12[F],
    sourceC: CadRecord12[F],
    pwfs1:   CadRecord12[F],
    pwfs2:   CadRecord12[F],
    oiwfs:   CadRecord12[F],
    g1:      CadRecord12[F],
    g2:      CadRecord12[F],
    g3:      CadRecord12[F],
    g4:      CadRecord12[F]
  ) extends TargetCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, TargetCmds[F]] = for {
    sourcea <- CadRecord12.build(server, top + SourceASuffix)
    sourceb <- CadRecord12.build(server, top + SourceBSuffix)
    sourcec <- CadRecord12.build(server, top + SourceCSuffix)
    pwfs1   <- CadRecord12.build(server, top + Pwfs1Suffix)
    pwfs2   <- CadRecord12.build(server, top + Pwfs2Suffix)
    oiwfs   <- CadRecord12.build(server, top + OiwfsSuffix)
    g1      <- CadRecord12.build(server, top + G1Suffix)
    g2      <- CadRecord12.build(server, top + G2Suffix)
    g3      <- CadRecord12.build(server, top + G3Suffix)
    g4      <- CadRecord12.build(server, top + G4Suffix)
  } yield TargetCmdsImpl(sourcea, sourceb, sourcec, pwfs1, pwfs2, oiwfs, g1, g2, g3, g4)
}
