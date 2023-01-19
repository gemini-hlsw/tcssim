// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait OdgwPark[F[_]] {
  val odgw1: MemoryPV1[F, String]
  val odgw2: MemoryPV1[F, String]
  val odgw3: MemoryPV1[F, String]
  val odgw4: MemoryPV1[F, String]
}

object OdgwPark {
  val Odgw1Name: String = "drives:odgw1Parked.VAL"
  val Odgw2Name: String = "drives:odgw2Parked.VAL"
  val Odgw3Name: String = "drives:odgw3Parked.VAL"
  val Odgw4Name: String = "drives:odgw4Parked.VAL"

  final case class OdgwParkImpl[F[_]] private (
    odgw1: MemoryPV1[F, String],
    odgw2: MemoryPV1[F, String],
    odgw3: MemoryPV1[F, String],
    odgw4: MemoryPV1[F, String]
  ) extends OdgwPark[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, OdgwPark[F]] = for {
    odgw1 <- server.createPV1(top + Odgw1Name, "")
    odgw2 <- server.createPV1(top + Odgw2Name, "")
    odgw3 <- server.createPV1(top + Odgw3Name, "")
    odgw4 <- server.createPV1(top + Odgw4Name, "")
  } yield OdgwParkImpl(odgw1, odgw2, odgw3, odgw4)
}
