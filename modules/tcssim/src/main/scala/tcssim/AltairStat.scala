// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait AltairStat[F[_]] {
  val prepMatrixX: MemoryPV1[F, String]
  val prepMatrixY: MemoryPV1[F, String]
  val aogsX: MemoryPV1[F, Double]
  val aogsY: MemoryPV1[F, Double]
}

object AltairStat {
  val PrepMatrixXName: String = "aoPrepareCm.VALA"
  val PrepMatrixYName: String = "aoPrepareCm.VALB"
  val AogsXName: String       = "drives:driveAOS.VALC"
  val AogsYName: String       = "drives:driveAOS.VALB"

  final case class AltairStatImpl[F[_]] private (
    prepMatrixX: MemoryPV1[F, String],
    prepMatrixY: MemoryPV1[F, String],
    aogsX:       MemoryPV1[F, Double],
    aogsY:       MemoryPV1[F, Double]
  ) extends AltairStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, AltairStat[F]] = for {
    pmx <- server.createPV1(top + PrepMatrixXName, 0.0.toString)
    pmy <- server.createPV1(top + PrepMatrixYName, 0.0.toString)
    gsx <- server.createPV1(top + AogsXName, 0.0)
    gsy <- server.createPV1(top + AogsYName, 0.0)
  } yield AltairStatImpl(pmx, pmy, gsx, gsy)
}
