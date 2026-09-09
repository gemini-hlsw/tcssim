// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class PointingCorrectionReadout[F[_]](
  caLocal: MemoryPV1[F, Double],
  ceLocal: MemoryPV1[F, Double],
  caGuide: MemoryPV1[F, Double],
  ceGuide: MemoryPV1[F, Double]
)

object PointingCorrectionReadout {
  def build[F[_]](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, PointingCorrectionReadout[F]] = for {
    cal <- server.createPV1[Double](s"${top}sad:calocal.VAL", 0.0)
    cel <- server.createPV1[Double](s"${top}sad:celocal.VAL", 0.0)
    cag <- server.createPV1[Double](s"${top}sad:caguide.VAL", 0.0)
    ceg <- server.createPV1[Double](s"${top}sad:ceguide.VAL", 0.0)
  } yield PointingCorrectionReadout(cal, cel, cag, ceg)

}
