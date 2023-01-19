// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait DomeVignette[F[_]] {
  val domeVignette: MemoryPV1[F, Double]
  val domeVignetteTime: MemoryPV1[F, Double]
}

object DomeVignette {
  val DomeVignetteSuffix: String     = "domeVignette.VAL"
  val DomeVignetteTimeSuffix: String = "timeToDomeLimit.VAL"

  final case class DomeVignetteImpl[F[_]] private (
    domeVignette:     MemoryPV1[F, Double],
    domeVignetteTime: MemoryPV1[F, Double]
  ) extends DomeVignette[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, DomeVignette[F]] = for {
    dv  <- server.createPV1(prefix + DomeVignetteSuffix, 0.0)
    dvt <- server.createPV1(prefix + DomeVignetteTimeSuffix, 0.0)
  } yield DomeVignetteImpl(dv, dvt)
}
