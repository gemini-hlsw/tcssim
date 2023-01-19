// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer

trait TcsEpicsDB[F[_]] {
  val status: TcsSad[F]
  val commands: TcsCommands[F]
}

object TcsEpicsDB {
  final case class TcsEpicsDBImpl[F[_]] private (
    status:   TcsSad[F],
    commands: TcsCommands[F]
  ) extends TcsEpicsDB[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, TcsEpicsDB[F]] = for {
    st  <- TcsSad.build(server, top)
    cmd <- TcsCommands.build(server, top)
  } yield TcsEpicsDBImpl(st, cmd)
}
