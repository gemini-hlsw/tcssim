// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil._

trait CadRecord16[F[_]] extends CadRecord12[F] {
  val inputM: MemoryPV1[F, String]
  val inputN: MemoryPV1[F, String]
  val inputO: MemoryPV1[F, String]
  val inputP: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] = super.inputs ++ List(inputM, inputN, inputO, inputP)
}

object CadRecord16 {

  private case class CadRecord16Impl[F[_]: Applicative](
    DIR:    MemoryPV1[F, CadDirective],
    override val MARK: MemoryPV1[F, Int],
    inputA: MemoryPV1[F, String],
    inputB: MemoryPV1[F, String],
    inputC: MemoryPV1[F, String],
    inputD: MemoryPV1[F, String],
    inputE: MemoryPV1[F, String],
    inputF: MemoryPV1[F, String],
    inputG: MemoryPV1[F, String],
    inputH: MemoryPV1[F, String],
    inputI: MemoryPV1[F, String],
    inputJ: MemoryPV1[F, String],
    inputK: MemoryPV1[F, String],
    inputL: MemoryPV1[F, String],
    inputM: MemoryPV1[F, String],
    inputN: MemoryPV1[F, String],
    inputO: MemoryPV1[F, String],
    inputP: MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F] with CadRecord16[F]

  def build[F[_]: Applicative](server: EpicsServer[F], cadName: String): Resource[F, CadRecord16[F]] = for {
    dir <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
    a   <- server.createPV1(cadName + InputASuffix, "")
    b   <- server.createPV1(cadName + InputBSuffix, "")
    c   <- server.createPV1(cadName + InputCSuffix, "")
    d   <- server.createPV1(cadName + InputDSuffix, "")
    e   <- server.createPV1(cadName + InputESuffix, "")
    f   <- server.createPV1(cadName + InputFSuffix, "")
    g   <- server.createPV1(cadName + InputGSuffix, "")
    h   <- server.createPV1(cadName + InputHSuffix, "")
    i   <- server.createPV1(cadName + InputISuffix, "")
    j   <- server.createPV1(cadName + InputJSuffix, "")
    k   <- server.createPV1(cadName + InputKSuffix, "")
    l   <- server.createPV1(cadName + InputLSuffix, "")
    m   <- server.createPV1(cadName + InputMSuffix, "")
    n   <- server.createPV1(cadName + InputNSuffix, "")
    o   <- server.createPV1(cadName + InputOSuffix, "")
    p   <- server.createPV1(cadName + InputPSuffix, "")
  } yield CadRecord16Impl(dir, mark, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)

}
