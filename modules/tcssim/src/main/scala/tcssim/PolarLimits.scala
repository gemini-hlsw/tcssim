// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait PolarLimits[F[_]] {
  val rmax: MemoryPV1[F, Double]
  val rmin: MemoryPV1[F, Double]
}

object PolarLimits {
  val RmaxSuffix: String = "Rmax.VAL"
  val RminSuffix: String = "Rmin.VAL"

  private case class PolarLimitsImpl[F[_]](
    rmax: MemoryPV1[F, Double],
    rmin: MemoryPV1[F, Double]
  ) extends PolarLimits[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, PolarLimits[F]] = for {
    rmax <- server.createPV1(prefix + RmaxSuffix, 0.0)
    rmin <- server.createPV1(prefix + RminSuffix, 0.0)
  } yield PolarLimitsImpl(rmax, rmin)
}
