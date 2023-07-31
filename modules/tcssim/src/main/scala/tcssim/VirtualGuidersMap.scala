// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait VirtualGuidersMap[F[_]] {
  val g1MapName: MemoryPV1[F, String]
  val g2MapName: MemoryPV1[F, String]
  val g3MapName: MemoryPV1[F, String]
  val g4MapName: MemoryPV1[F, String]
}

object VirtualGuidersMap {
  val G1MapNameSuffix: String = "guiderConfig.VALA"
  val G2MapNameSuffix: String = "guiderConfig.VALB"
  val G3MapNameSuffix: String = "guiderConfig.VALC"
  val G4MapNameSuffix: String = "guiderConfig.VALD"

  case class VirtualGuidersMapImpl[F[_]](
    g1MapName: MemoryPV1[F, String],
    g2MapName: MemoryPV1[F, String],
    g3MapName: MemoryPV1[F, String],
    g4MapName: MemoryPV1[F, String]
  ) extends VirtualGuidersMap[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, VirtualGuidersMap[F]] = for {
    g1 <- server.createPV1(top + G1MapNameSuffix, "NONE")
    g2 <- server.createPV1(top + G2MapNameSuffix, "NONE")
    g3 <- server.createPV1(top + G3MapNameSuffix, "NONE")
    g4 <- server.createPV1(top + G4MapNameSuffix, "NONE")
  } yield VirtualGuidersMapImpl(g1, g2, g3, g4)

}
