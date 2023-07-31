// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1

import CadUtil._

trait CadRecord[F[_]] {
  val DIR: MemoryPV1[F, CadDirective]
}

object CadRecord {

  private case class CadRecordImpl[F[_]](
    DIR: MemoryPV1[F, CadDirective]
  ) extends CadRecord[F]

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, CadRecord[F]] = for {
    dir <- buildDir(server, name)
  } yield CadRecordImpl(dir)
}
