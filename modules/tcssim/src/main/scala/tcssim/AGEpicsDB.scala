// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.kernel.Resource
import cats.syntax.all.*
import fs2.Stream
import tcssim.epics.EpicsServer

trait AGEpicsDB[F[_]] {
  val status: AG[F]
  val commands: AGCmds[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object AGEpicsDB {
  private case class AGEpicsDBImpl[F[_]: Monad](
    status:   AG[F],
    commands: AGCmds[F]
  ) extends AGEpicsDB[F] {
    override def process: Resource[F, List[Stream[F, Unit]]] =
      commands.cads.map(_.process).sequence.map(_.flatten)

    override def clean: F[Unit] = commands.cads.map(_.clean).sequence.void
  }

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, AGEpicsDB[F]] =
    for {
      st  <- AG.build(server, top)
      cmd <- AGCmds.build(server, top)
    } yield AGEpicsDBImpl(st, cmd)
}
