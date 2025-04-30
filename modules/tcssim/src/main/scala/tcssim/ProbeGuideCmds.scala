// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait ProbeGuideCmds[F[_]] {
  val pwfs1: CadRecord9[F]
  val pwfs2: CadRecord9[F]
  val oiwfs: CadRecord9[F]
  val g1: CadRecord9[F]
  val g2: CadRecord9[F]
  val g3: CadRecord9[F]
  val g4: CadRecord9[F]
  val cads: List[CadRecord[F]]
}

object ProbeGuideCmds {

  private def buildProbeGuideCmd[F[_]: Monad](
    server: EpicsServer[F],
    top:    String,
    guider: String
  ): Resource[F, CadRecord9[F]] =
    CadRecord9.build(server, s"${top}config$guider")

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, ProbeGuideCmds[F]] =
    for {
      p1  <- buildProbeGuideCmd(server, top, "Pwfs1")
      p2  <- buildProbeGuideCmd(server, top, "Pwfs2")
      oi  <- buildProbeGuideCmd(server, top, "Oiwfs")
      ao1 <- buildProbeGuideCmd(server, top, "G1")
      ao2 <- buildProbeGuideCmd(server, top, "G2")
      ao3 <- buildProbeGuideCmd(server, top, "G3")
      ao4 <- buildProbeGuideCmd(server, top, "G4")
    } yield new ProbeGuideCmds[F] {
      override val pwfs1: CadRecord9[F]     = p1
      override val pwfs2: CadRecord9[F]     = p2
      override val oiwfs: CadRecord9[F]     = oi
      override val g1: CadRecord9[F]        = ao1
      override val g2: CadRecord9[F]        = ao2
      override val g3: CadRecord9[F]        = ao3
      override val g4: CadRecord9[F]        = ao4
      override val cads: List[CadRecord[F]] = List(p1, p2, oi, ao1, ao2, ao3, ao4)
    }

}
