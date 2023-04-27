// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait CurrentCoords[F[_]] {
  val currentRA: MemoryPV1[F, Double]
  val currentDec: MemoryPV1[F, Double]
  val currentAz: MemoryPV1[F, Double]
  val currentEl: MemoryPV1[F, Double]
  val currentHAS: MemoryPV1[F, String]
  val currentRma: MemoryPV1[F, Double]
}

object CurrentCoords {
  val CurrentRASuffix: String  = "telescopeRA.VAL"
  val CurrentDecSuffix: String = "telescopeDec.VAL"
  val CurrentAzSuffix: String  = "currentAz.VAL"
  val CurrentElSuffix: String  = "currentEl.VAL"
  val CurrentHASName: String   = "currentHAString.VAL"
  val CurrentRmaName: String   = "currentRma.VAL"

  final case class CurrentCoordsImpl[F[_]] private (
    currentRA:  MemoryPV1[F, Double],
    currentDec: MemoryPV1[F, Double],
    currentAz:  MemoryPV1[F, Double],
    currentEl:  MemoryPV1[F, Double],
    currentHAS: MemoryPV1[F, String],
    currentRma: MemoryPV1[F, Double]
  ) extends CurrentCoords[F]

  def build[F[_]](
    server: EpicsServer[F],
    top:    String,
    prefix: String
  ): Resource[F, CurrentCoords[F]] = for {
    cra  <- server.createPV1(top + CurrentRASuffix, 0.0)
    cdec <- server.createPV1(top + CurrentDecSuffix, 0.0)
    caz  <- server.createPV1(top + CurrentAzSuffix, 0.0)
    cel  <- server.createPV1(top + CurrentElSuffix, 0.0)
    chas <- server.createPV1(prefix + CurrentHASName, "00:00:00")
    rma  <- server.createPV1(top + CurrentRmaName, 0.0)
  } yield CurrentCoordsImpl(cra, cdec, caz, cel, chas, rma)

}
