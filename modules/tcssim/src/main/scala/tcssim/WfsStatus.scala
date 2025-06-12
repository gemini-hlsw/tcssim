// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class WfsStatus[F[_]](
  telltale: MemoryPV1[F, String],
  flux:     MemoryPV1[F, Int],
  centroid: MemoryPV1[F, Int]
)

object WfsStatus {
  def build[F[_]](
    server:       EpicsServer[F],
    top:          String,
    telltaleName: String,
    fluxName:     String,
    centroidName: String
  ): Resource[F, WfsStatus[F]] = for {
    tell <- server.createPV1(top + telltaleName, "")
    flux <- server.createPV1(top + fluxName, 0)
    cntr <- server.createPV1(top + centroidName, 0)
  } yield WfsStatus(tell, flux, cntr)
}
