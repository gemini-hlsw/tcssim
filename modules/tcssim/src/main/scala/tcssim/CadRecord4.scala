// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import CadUtil._

trait CadRecord4[F[_]] extends CadRecord[F] {
  val inputA: MemoryPV1[F, String]
  val inputB: MemoryPV1[F, String]
  val inputC: MemoryPV1[F, String]
  val inputD: MemoryPV1[F, String]
}

object CadRecord4 {

  final case class CadRecord4Impl[F[_]] private (
    DIR:    MemoryPV1[F, CadDirective],
    inputA: MemoryPV1[F, String],
    inputB: MemoryPV1[F, String],
    inputC: MemoryPV1[F, String],
    inputD: MemoryPV1[F, String]
  ) extends CadRecord4[F]

  def build[F[_]](server: EpicsServer[F], cadName: String): Resource[F, CadRecord4[F]] = for {
    dir <- buildDir(server, cadName)
    a   <- server.createPV1(cadName + InputASuffix, "")
    b   <- server.createPV1(cadName + InputBSuffix, "")
    c   <- server.createPV1(cadName + InputCSuffix, "")
    d   <- server.createPV1(cadName + InputDSuffix, "")
  } yield CadRecord4Impl(dir, a, b, c, d)
}
