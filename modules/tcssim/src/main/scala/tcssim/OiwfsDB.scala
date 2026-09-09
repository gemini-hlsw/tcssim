// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import fs2.Stream
import tcssim.epics.EpicsServer

trait OiwfsDB[F[_]] {
  val status: WfsStatus[F]
  val commands: OiwfsCommands[F]
  val wfsCommands: WfsCommands[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object OiwfsDB {
  def build[F[_]: Monad](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, OiwfsDB[F]] = for {
    sta  <- WfsStatus.build(server, top, "dc:seeing.VAL", "dc:fgDiag1P2.VALQ", "dc:fgDiag1P2.VALB")
    cmd  <- OiwfsCommands.build(server, top)
    wfsc <- WfsCommands.build[F](server, top, "dc:initSigInitFgGain.PROC")
  } yield new OiwfsDB[F] {
    override val status: WfsStatus[F]        = sta
    override val commands: OiwfsCommands[F]  = cmd
    override val wfsCommands: WfsCommands[F] = wfsc

    override def process: Resource[F, List[Stream[F, Unit]]] =
      (commands.cads ++ wfsCommands.cads).map(_.process).sequence.map(_.flatten)

    override def clean: F[Unit] = (commands.cads ++ wfsCommands.cads).map(_.clean).sequence.void
  }
}
