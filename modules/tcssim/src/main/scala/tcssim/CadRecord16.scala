// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil.*

trait CadRecord16[F[_]] extends CadRecord12[F] {
  val inputM: MemoryPV1[F, String]
  val inputN: MemoryPV1[F, String]
  val inputO: MemoryPV1[F, String]
  val inputP: MemoryPV1[F, String]
  val outputM: MemoryPV1[F, String]
  val outputN: MemoryPV1[F, String]
  val outputO: MemoryPV1[F, String]
  val outputP: MemoryPV1[F, String]
  override def inputs: List[MemoryPV1[F, String]] =
    super.inputs ++ List(inputM, inputN, inputO, inputP)
}

object CadRecord16 {

  private case class CadRecord16Impl[F[_]: Monad](
    override val DIR:     MemoryPV1[F, CadDirective],
    override val MARK:    MemoryPV1[F, Int],
    override val inputA:  MemoryPV1[F, String],
    override val inputB:  MemoryPV1[F, String],
    override val inputC:  MemoryPV1[F, String],
    override val inputD:  MemoryPV1[F, String],
    override val inputE:  MemoryPV1[F, String],
    override val inputF:  MemoryPV1[F, String],
    override val inputG:  MemoryPV1[F, String],
    override val inputH:  MemoryPV1[F, String],
    override val inputI:  MemoryPV1[F, String],
    override val inputJ:  MemoryPV1[F, String],
    override val inputK:  MemoryPV1[F, String],
    override val inputL:  MemoryPV1[F, String],
    override val inputM:  MemoryPV1[F, String],
    override val inputN:  MemoryPV1[F, String],
    override val inputO:  MemoryPV1[F, String],
    override val inputP:  MemoryPV1[F, String],
    override val outputA: MemoryPV1[F, String],
    override val outputB: MemoryPV1[F, String],
    override val outputC: MemoryPV1[F, String],
    override val outputD: MemoryPV1[F, String],
    override val outputE: MemoryPV1[F, String],
    override val outputF: MemoryPV1[F, String],
    override val outputG: MemoryPV1[F, String],
    override val outputH: MemoryPV1[F, String],
    override val outputI: MemoryPV1[F, String],
    override val outputJ: MemoryPV1[F, String],
    override val outputK: MemoryPV1[F, String],
    override val outputL: MemoryPV1[F, String],
    override val outputM: MemoryPV1[F, String],
    override val outputN: MemoryPV1[F, String],
    override val outputO: MemoryPV1[F, String],
    override val outputP: MemoryPV1[F, String]
  ) extends CadRecord.CadRecordImpl[F]
      with CadRecord16[F]

  def build[F[_]: Monad](
    server:  EpicsServer[F],
    cadName: String
  ): Resource[F, CadRecord16[F]] = for {
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
    m    <- server.createPV1(cadName + InputMSuffix, "")
    n    <- server.createPV1(cadName + InputNSuffix, "")
    o    <- server.createPV1(cadName + InputOSuffix, "")
    p    <- server.createPV1(cadName + InputPSuffix, "")
    vala <- server.createPV1(cadName + OutputASuffix, "")
    valb <- server.createPV1(cadName + OutputBSuffix, "")
    valc <- server.createPV1(cadName + OutputCSuffix, "")
    vald <- server.createPV1(cadName + OutputDSuffix, "")
    vale <- server.createPV1(cadName + OutputESuffix, "")
    valf <- server.createPV1(cadName + OutputFSuffix, "")
    valg <- server.createPV1(cadName + OutputGSuffix, "")
    valh <- server.createPV1(cadName + OutputHSuffix, "")
    vali <- server.createPV1(cadName + OutputISuffix, "")
    valj <- server.createPV1(cadName + OutputJSuffix, "")
    valk <- server.createPV1(cadName + OutputKSuffix, "")
    vall <- server.createPV1(cadName + OutputLSuffix, "")
    valm <- server.createPV1(cadName + OutputMSuffix, "")
    valn <- server.createPV1(cadName + OutputNSuffix, "")
    valo <- server.createPV1(cadName + OutputOSuffix, "")
    valp <- server.createPV1(cadName + OutputPSuffix, "")
  } yield CadRecord16Impl(dir,
                          mark,
                          a,
                          b,
                          c,
                          d,
                          e,
                          f,
                          g,
                          h,
                          i,
                          j,
                          k,
                          l,
                          m,
                          n,
                          o,
                          p,
                          vala,
                          valb,
                          valc,
                          vald,
                          vale,
                          valf,
                          valg,
                          valh,
                          vali,
                          valj,
                          valk,
                          vall,
                          valm,
                          valn,
                          valo,
                          valp
  )

}
