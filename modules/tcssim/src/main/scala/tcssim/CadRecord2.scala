// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil._

trait CadRecord2[F[_]] extends CadRecord1[F] {
  val inputB: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs :+ inputB
}

object CadRecord2 {

  private case class CadRecord2Impl[F[_]: Applicative](
    override val DIR:    MemoryPV1[F, CadDirective],
    override val MARK: MemoryPV1[F, Int],
    override val inputA: MemoryPV1[F, String],
    inputB: MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F] with CadRecord2[F]

  def build[F[_]: Applicative](server: EpicsServer[F], cadName: String): Resource[F, CadRecord2[F]] = for {
    dir <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a   <- server.createPV1(cadName + InputASuffix, "")
    b   <- server.createPV1(cadName + InputBSuffix, "")
  } yield CadRecord2Impl(dir, mark, a, b)
}
