// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.kernel.Resource
import cats.syntax.all.*
import fs2.Stream
import tcssim.epics.EpicsServer

trait TcsEpicsDB[F[_]] {
  val status: TcsSad[F]
  val commands: TcsCommands[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object TcsEpicsDB {
  private case class TcsEpicsDBImpl[F[_]: Monad](
    status:   TcsSad[F],
    commands: TcsCommands[F]
  ) extends TcsEpicsDB[F] {
    override def process: Resource[F, List[Stream[F, Unit]]] =
      commands.cads.map(_.process).sequence.map(_.flatten)

    override def clean: F[Unit] = commands.cads.map(_.clean).sequence.void
  }

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, TcsEpicsDB[F]] =
    for {
      st  <- TcsSad.build(server, top)
      cmd <- TcsCommands.build(server, top)
    } yield TcsEpicsDBImpl(st, cmd)
}
