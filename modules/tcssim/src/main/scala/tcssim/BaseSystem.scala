// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class BaseSystem[F[_]](
  health:  MemoryPV1[F, String],
  followS: MemoryPV1[F, String]
)

object BaseSystem {
  val Health: String = "health.VAL"
  val Follow: String = "followS.VAL"

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, BaseSystem[F]] = for {
    hlt    <- server.createPV1(top + Health, "GOOD")
    follow <- server.createPV1(top + Follow, "On")
  } yield BaseSystem(hlt, follow)

}
