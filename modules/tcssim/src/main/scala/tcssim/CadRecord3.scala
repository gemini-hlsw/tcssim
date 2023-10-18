// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil._

trait CadRecord3[F[_]] extends CadRecord2[F] {
  val inputC: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs :+ inputC
}

object CadRecord3 {

  private case class CadRecord3Impl[F[_]: Applicative](
    override val DIR:    MemoryPV1[F, CadDirective],
    override val MARK:   MemoryPV1[F, Int],
    override val inputA: MemoryPV1[F, String],
    override val inputB: MemoryPV1[F, String],
    override val inputC: MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F]
      with CadRecord3[F]

  def build[F[_]: Applicative](
    server:  EpicsServer[F],
    cadName: String
  ): Resource[F, CadRecord3[F]] = for {
    dir  <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a    <- server.createPV1(cadName + InputASuffix, "")
    b    <- server.createPV1(cadName + InputBSuffix, "")
    c    <- server.createPV1(cadName + InputCSuffix, "")
  } yield CadRecord3Impl(dir, mark, a, b, c)
}
