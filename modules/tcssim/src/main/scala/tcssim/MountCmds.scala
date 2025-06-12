// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait MountCmds[F[_]] {
  val park: CadRecord[F]

  def cads: List[CadRecord[F]]
}

object MountCmds {
  val mountParkSuffix: String = "telpark"

  private case class MountCmdsImpl[F[_]](
    park: CadRecord[F]
  ) extends MountCmds[F]:
    override def cads: List[CadRecord[F]] = List(park)

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, MountCmds[F]] =
    for {
      park <- CadRecord.build(server, top + mountParkSuffix)
    } yield MountCmdsImpl(park)
}
