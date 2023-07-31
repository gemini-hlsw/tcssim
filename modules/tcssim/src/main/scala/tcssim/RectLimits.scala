// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait RectLimits[F[_]] {
  val xmax: MemoryPV1[F, Double]
  val xmin: MemoryPV1[F, Double]
  val ymax: MemoryPV1[F, Double]
  val ymin: MemoryPV1[F, Double]
}

object RectLimits {
  val XmaxSuffix: String = "Xmax.VAL"
  val XminSuffix: String = "Xmin.VAL"
  val YmaxSuffix: String = "Ymax.VAL"
  val YminSuffix: String = "Ymin.VAL"

  private case class RectLimitsImpl[F[_]](
    xmax: MemoryPV1[F, Double],
    xmin: MemoryPV1[F, Double],
    ymax: MemoryPV1[F, Double],
    ymin: MemoryPV1[F, Double]
  ) extends RectLimits[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, RectLimits[F]] = for {
    xmax <- server.createPV1(prefix + XmaxSuffix, 0.0)
    xmin <- server.createPV1(prefix + XminSuffix, 0.0)
    ymax <- server.createPV1(prefix + YmaxSuffix, 0.0)
    ymin <- server.createPV1(prefix + YminSuffix, 0.0)
  } yield RectLimitsImpl(xmax, xmin, ymax, ymin)
}
