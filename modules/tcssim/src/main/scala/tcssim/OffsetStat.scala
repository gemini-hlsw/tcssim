// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait OffsetStat[F[_]] {
  val xOffsetPoA1: MemoryPV1[F, Double]
  val yOffsetPoA1: MemoryPV1[F, Double]
  val xOffsetPoB1: MemoryPV1[F, Double]
  val yOffsetPoB1: MemoryPV1[F, Double]
  val xOffsetPoC1: MemoryPV1[F, Double]
  val yOffsetPoC1: MemoryPV1[F, Double]
}

object OffsetStat {
  val XOffsetPoA1Name: String = "offsetPoA1.VALA"
  val YOffsetPoA1Name: String = "offsetPoA1.VALB"
  val XOffsetPoB1Name: String = "offsetPoB1.VALA"
  val YOffsetPoB1Name: String = "offsetPoB1.VALB"
  val XOffsetPoC1Name: String = "offsetPoC1.VALA"
  val YOffsetPoC1Name: String = "offsetPoC1.VALB"

  final case class OffsetStatImpl[F[_]] private (
    xOffsetPoA1: MemoryPV1[F, Double],
    yOffsetPoA1: MemoryPV1[F, Double],
    xOffsetPoB1: MemoryPV1[F, Double],
    yOffsetPoB1: MemoryPV1[F, Double],
    xOffsetPoC1: MemoryPV1[F, Double],
    yOffsetPoC1: MemoryPV1[F, Double]
  ) extends OffsetStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, OffsetStat[F]] = for {
    xoffsetpoa1 <- server.createPV1(top + XOffsetPoA1Name, 0.0)
    yoffsetpoa1 <- server.createPV1(top + YOffsetPoA1Name, 0.0)
    xoffsetpob1 <- server.createPV1(top + XOffsetPoB1Name, 0.0)
    yoffsetpob1 <- server.createPV1(top + YOffsetPoB1Name, 0.0)
    xoffsetpoc1 <- server.createPV1(top + XOffsetPoC1Name, 0.0)
    yoffsetpoc1 <- server.createPV1(top + YOffsetPoC1Name, 0.0)
  } yield OffsetStatImpl(xoffsetpoa1,
                         yoffsetpoa1,
                         xoffsetpob1,
                         yoffsetpob1,
                         xoffsetpoc1,
                         yoffsetpoc1
  )
}
