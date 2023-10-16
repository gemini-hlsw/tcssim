// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil._

trait CadRecord1[F[_]] extends CadRecord[F] {
  val inputA: MemoryPV1[F, String]
}

object CadRecord1 {
  private case class CadRecord1Impl[F[_]: Applicative](
    override val DIR:  MemoryPV1[F, CadDirective],
    override val MARK: MemoryPV1[F, Int],
    inputA: MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F] with CadRecord1[F] {
    override def inputs: List[MemoryPV1[F, String]] = List(inputA)
  }

  def build[F[_]: Applicative](server: EpicsServer[F], cadName: String): Resource[F, CadRecord1[F]] = for {
    dir <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a   <- server.createPV1(cadName + InputASuffix, "")
  } yield CadRecord1Impl(dir, mark, a)
}
