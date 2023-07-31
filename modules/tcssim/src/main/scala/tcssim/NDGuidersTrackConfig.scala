// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait NDGuidersTrackConfig[F[_]] {
  val pwfs1: NodChopGuide[F]
  val pwfs2: NodChopGuide[F]
  val oiwfs: NodChopGuide[F]
  val g1: NodChopGuide[F]
  val g2: NodChopGuide[F]
  val g3: NodChopGuide[F]
  val g4: NodChopGuide[F]
}

object NDGuidersTrackConfig {
  val Pwfs1Name: String = "Pwfs1"
  val Pwfs2Name: String = "Pwfs2"
  val OiwfsName: String = "Oiwfs"
  val G1Name: String    = "G1"
  val G2Name: String    = "G2"
  val G3Name: String    = "G3"
  val G4Name: String    = "G4"

  private case class NDGuidersTrackConfigImpl[F[_]](
    pwfs1: NodChopGuide[F],
    pwfs2: NodChopGuide[F],
    oiwfs: NodChopGuide[F],
    g1:    NodChopGuide[F],
    g2:    NodChopGuide[F],
    g3:    NodChopGuide[F],
    g4:    NodChopGuide[F]
  ) extends NDGuidersTrackConfig[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, NDGuidersTrackConfig[F]] = for {
    p1 <- NodChopGuide.build(server, top, Pwfs1Name)
    p2 <- NodChopGuide.build(server, top, Pwfs2Name)
    oi <- NodChopGuide.build(server, top, OiwfsName)
    g1 <- NodChopGuide.build(server, top, G1Name)
    g2 <- NodChopGuide.build(server, top, G2Name)
    g3 <- NodChopGuide.build(server, top, G3Name)
    g4 <- NodChopGuide.build(server, top, G4Name)
  } yield NDGuidersTrackConfigImpl(p1, p2, oi, g1, g2, g3, g4)

}
