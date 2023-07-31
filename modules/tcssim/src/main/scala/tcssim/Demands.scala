// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait Demands[F[_]] {
  val demandAz: MemoryPV1[F, Double]
  val demandEl: MemoryPV1[F, Double]
  val demandRma: MemoryPV1[F, Double]
  val demandRA: MemoryPV1[F, Double]
  val demandDec: MemoryPV1[F, Double]
}

object Demands {
  val DemandAzSuffix: String  = "demandAz.VAL"
  val DemandElSuffix: String  = "demandEl.VAL"
  val DemandRmaSuffix: String = "demandRma.VAL"
  val DemandRASuffix: String  = "demandRA.VAL"
  val DemandDecSuffix: String = "demandDec.VAL"

  private case class DemandsImpl[F[_]](
    demandAz:  MemoryPV1[F, Double],
    demandEl:  MemoryPV1[F, Double],
    demandRma: MemoryPV1[F, Double],
    demandRA:  MemoryPV1[F, Double],
    demandDec: MemoryPV1[F, Double]
  ) extends Demands[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, Demands[F]] = for {
    az  <- server.createPV1(prefix + DemandAzSuffix, 0.0)
    el  <- server.createPV1(prefix + DemandElSuffix, 0.0)
    rma <- server.createPV1(prefix + DemandRmaSuffix, 0.0)
    ra  <- server.createPV1(prefix + DemandRASuffix, 0.0)
    dec <- server.createPV1(prefix + DemandDecSuffix, 0.0)
  } yield DemandsImpl(az, el, rma, ra, dec)

}
