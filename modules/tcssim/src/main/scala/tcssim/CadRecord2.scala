// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import CadUtil._

trait CadRecord2[F[_]] extends CadRecord[F] {
  val inputA: MemoryPV1[F, String]
  val inputB: MemoryPV1[F, String]
}

object CadRecord2 {

  final case class CadRecord2Impl[F[_]] private (
    DIR:    MemoryPV1[F, CadDirective],
    inputA: MemoryPV1[F, String],
    inputB: MemoryPV1[F, String]
  ) extends CadRecord2[F]

  def build[F[_]](server: EpicsServer[F], cadName: String): Resource[F, CadRecord2[F]] = for {
    dir <- buildDir(server, cadName)
    a   <- server.createPV1(cadName + InputASuffix, "")
    b   <- server.createPV1(cadName + InputBSuffix, "")
  } yield CadRecord2Impl(dir, a, b)
}
