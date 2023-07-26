// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait MountCmds[F[_]] {
  val park: CadRecord[F]
}

object MountCmds {
  val mountParkSuffix: String = "telpark"

  final case class MountCmdsImpl[F[_]] private (
    park: CadRecord[F]
  ) extends MountCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, MountCmds[F]] = for {
    park <- CadRecord.build(server, top + mountParkSuffix)
  } yield MountCmdsImpl(park)
}
