// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait AGStat[F[_]] {
  val hrPark: MemoryPV1[F, String]
}

object AGStat {
  val HrParkName: String = "drives:agHwName.VAL"

  final case class AGStatImpl[F[_]] private (
    hrPark: MemoryPV1[F, String]
  ) extends AGStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, AGStat[F]] = for {
    hrpark <- server.createPV1(top + HrParkName, "Out")
  } yield AGStatImpl(hrpark)
}
