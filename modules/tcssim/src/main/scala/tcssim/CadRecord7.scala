// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
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
  val outputG: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs :+ inputG
}

object CadRecord7 {

  private case class CadRecord7Impl[F[_]: Monad](
    override val DIR:     MemoryPV1[F, CadDirective],
    override val MARK:    MemoryPV1[F, Int],
    override val inputA:  MemoryPV1[F, String],
    override val inputB:  MemoryPV1[F, String],
    override val inputC:  MemoryPV1[F, String],
    override val inputD:  MemoryPV1[F, String],
    override val inputE:  MemoryPV1[F, String],
    override val inputF:  MemoryPV1[F, String],
    override val inputG:  MemoryPV1[F, String],
    override val outputA: MemoryPV1[F, String],
    override val outputB: MemoryPV1[F, String],
    override val outputC: MemoryPV1[F, String],
    override val outputD: MemoryPV1[F, String],
    override val outputE: MemoryPV1[F, String],
    override val outputF: MemoryPV1[F, String],
    override val outputG: MemoryPV1[F, String]
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
    vala <- server.createPV1(cadName + OutputASuffix, "")
    valb <- server.createPV1(cadName + OutputBSuffix, "")
    valc <- server.createPV1(cadName + OutputCSuffix, "")
    vald <- server.createPV1(cadName + OutputDSuffix, "")
    vale <- server.createPV1(cadName + OutputESuffix, "")
    valf <- server.createPV1(cadName + OutputFSuffix, "")
    valg <- server.createPV1(cadName + OutputGSuffix, "")
  } yield CadRecord7Impl(dir, mark, a, b, c, d, e, f, g, vala, valb, valc, vald, vale, valf, valg)

}
