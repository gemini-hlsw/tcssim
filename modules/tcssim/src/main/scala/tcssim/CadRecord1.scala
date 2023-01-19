// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import CadUtil._

trait CadRecord1[F[_]] extends CadRecord[F] {
  val inputA: MemoryPV1[F, String]
}

object CadRecord1 {
  final case class CadRecord1Impl[F[_]] private (
    DIR:    MemoryPV1[F, CadDirective],
    inputA: MemoryPV1[F, String]
  ) extends CadRecord1[F]

  def build[F[_]](server: EpicsServer[F], cadName: String): Resource[F, CadRecord1[F]] = for {
    dir <- buildDir(server, cadName)
    a   <- server.createPV1(cadName + InputASuffix, "")
  } yield CadRecord1Impl(dir, a)
}
