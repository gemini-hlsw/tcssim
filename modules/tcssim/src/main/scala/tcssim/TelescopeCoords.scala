// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait TelescopeCoords[F[_]] {
  val tlatm: MemoryPV1[F, Double]
  val tlongm: MemoryPV1[F, Double]
  val height: MemoryPV1[F, Double]
}

object TelescopeCoords {
  val TlatmSuffix: String  = "tlatm"
  val TlongmSuffix: String = "tlongm"
  val HeightSuffix: String = "height"

  final case class TelescopeCoordsImpl[F[_]] private (
    tlatm:  MemoryPV1[F, Double],
    tlongm: MemoryPV1[F, Double],
    height: MemoryPV1[F, Double]
  ) extends TelescopeCoords[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, TelescopeCoords[F]] = for {
    tlatlm <- server.createPV1(prefix + TlatmSuffix, 0.0)
    tlongm <- server.createPV1(prefix + TlongmSuffix, 0.0)
    height <- server.createPV1(prefix + HeightSuffix, 0.0)
  } yield TelescopeCoordsImpl(tlatlm, tlongm, height)
}
