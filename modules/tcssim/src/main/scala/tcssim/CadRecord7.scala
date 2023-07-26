// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import tcssim.epics.given
import CadUtil._

trait CadRecord7[F[_]] extends CadRecord[F] {
  val inputA: MemoryPV1[F, String]
  val inputB: MemoryPV1[F, String]
  val inputC: MemoryPV1[F, String]
  val inputD: MemoryPV1[F, String]
  val inputE: MemoryPV1[F, String]
  val inputF: MemoryPV1[F, String]
  val inputG: MemoryPV1[F, String]
}

object CadRecord7 {

  private case class CadRecord7Impl[F[_]](
    DIR:    MemoryPV1[F, CadDirective],
    inputA: MemoryPV1[F, String],
    inputB: MemoryPV1[F, String],
    inputC: MemoryPV1[F, String],
    inputD: MemoryPV1[F, String],
    inputE: MemoryPV1[F, String],
    inputF: MemoryPV1[F, String],
    inputG: MemoryPV1[F, String]
  ) extends CadRecord7[F]

  def build[F[_]](server: EpicsServer[F], cadName: String): Resource[F, CadRecord7[F]] = for {
    dir <- buildDir(server, cadName)
    a   <- server.createPV1(cadName + InputASuffix, "")
    b   <- server.createPV1(cadName + InputBSuffix, "")
    c   <- server.createPV1(cadName + InputCSuffix, "")
    d   <- server.createPV1(cadName + InputDSuffix, "")
    e   <- server.createPV1(cadName + InputESuffix, "")
    f   <- server.createPV1(cadName + InputFSuffix, "")
    g   <- server.createPV1(cadName + InputGSuffix, "")
  } yield CadRecord7Impl(dir, a, b, c, d, e, f, g)
}
