// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import CadUtil._

trait CadRecord[F[_]] {
  val DIR: MemoryPV1[F, CadDirective]
}

object CadRecord {

  final case class CadRecordImpl[F[_]] private (
    DIR: MemoryPV1[F, CadDirective]
  ) extends CadRecord[F]

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, CadRecord[F]] = for {
    dir <- buildDir(server, name)
  } yield CadRecordImpl(dir)
}
