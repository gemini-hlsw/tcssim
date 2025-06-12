// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil.*

trait CadRecord12[F[_]] extends CadRecord9[F] {
  val inputJ: MemoryPV1[F, String]
  val inputK: MemoryPV1[F, String]
  val inputL: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs ++ List(inputJ, inputK, inputL)
}

object CadRecord12 {

  private case class CadRecord12Impl[F[_]: Monad](
    DIR:               MemoryPV1[F, CadDirective],
    override val MARK: MemoryPV1[F, Int],
    inputA:            MemoryPV1[F, String],
    inputB:            MemoryPV1[F, String],
    inputC:            MemoryPV1[F, String],
    inputD:            MemoryPV1[F, String],
    inputE:            MemoryPV1[F, String],
    inputF:            MemoryPV1[F, String],
    inputG:            MemoryPV1[F, String],
    inputH:            MemoryPV1[F, String],
    inputI:            MemoryPV1[F, String],
    inputJ:            MemoryPV1[F, String],
    inputK:            MemoryPV1[F, String],
    inputL:            MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F]
      with CadRecord12[F]

  def build[F[_]: Monad](
    server:  EpicsServer[F],
    cadName: String
  ): Resource[F, CadRecord12[F]] = for {
    dir  <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a    <- server.createPV1(cadName + InputASuffix, "")
    b    <- server.createPV1(cadName + InputBSuffix, "")
    c    <- server.createPV1(cadName + InputCSuffix, "")
    d    <- server.createPV1(cadName + InputDSuffix, "")
    e    <- server.createPV1(cadName + InputESuffix, "")
    f    <- server.createPV1(cadName + InputFSuffix, "")
    g    <- server.createPV1(cadName + InputGSuffix, "")
    h    <- server.createPV1(cadName + InputHSuffix, "")
    i    <- server.createPV1(cadName + InputISuffix, "")
    j    <- server.createPV1(cadName + InputJSuffix, "")
    k    <- server.createPV1(cadName + InputKSuffix, "")
    l    <- server.createPV1(cadName + InputLSuffix, "")
  } yield CadRecord12Impl(dir, mark, a, b, c, d, e, f, g, h, i, j, k, l)
}
