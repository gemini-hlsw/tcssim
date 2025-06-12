// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import fs2.Stream
import tcssim.epics.EpicsServer

trait AcDB[F[_]] {
  val status: AcStatus[F]
  val commands: AcCommands[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object AcDB {
  def build[F[_]: Monad](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, AcDB[F]] = for {
    sta <- AcStatus.build(server, top)
    cmd <- AcCommands.build[F](server, top)
  } yield new AcDB[F] {
    override val status: AcStatus[F]     = sta
    override val commands: AcCommands[F] = cmd

    override def process: Resource[F, List[Stream[F, Unit]]] =
      commands.cads.map(_.process).sequence.map(_.flatten)

    override def clean: F[Unit] = commands.cads.map(_.clean).sequence.void
  }
}
