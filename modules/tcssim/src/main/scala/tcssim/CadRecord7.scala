// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil.*

trait CadRecord7[F[_]] extends CadRecord6[F] {
  val inputG: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs :+ inputG
}

object CadRecord7 {

  private case class CadRecord7Impl[F[_]: Monad](
    DIR:               MemoryPV1[F, CadDirective],
    override val MARK: MemoryPV1[F, Int],
    inputA:            MemoryPV1[F, String],
    inputB:            MemoryPV1[F, String],
    inputC:            MemoryPV1[F, String],
    inputD:            MemoryPV1[F, String],
    inputE:            MemoryPV1[F, String],
    inputF:            MemoryPV1[F, String],
    inputG:            MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F]
      with CadRecord7[F]

  def build[F[_]: Monad](
    server:  EpicsServer[F],
    cadName: String
  ): Resource[F, CadRecord7[F]] = for {
    dir  <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a    <- server.createPV1(cadName + InputASuffix, "")
    b    <- server.createPV1(cadName + InputBSuffix, "")
    c    <- server.createPV1(cadName + InputCSuffix, "")
    d    <- server.createPV1(cadName + InputDSuffix, "")
    e    <- server.createPV1(cadName + InputESuffix, "")
    f    <- server.createPV1(cadName + InputFSuffix, "")
    g    <- server.createPV1(cadName + InputGSuffix, "")
  } yield CadRecord7Impl(dir, mark, a, b, c, d, e, f, g)

}
