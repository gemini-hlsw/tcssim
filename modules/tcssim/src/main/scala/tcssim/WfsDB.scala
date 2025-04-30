// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import fs2.Stream
import tcssim.epics.EpicsServer

trait WfsDB[F[_]] {
  val status: WfsStatus[F]
  val commands: WfsCommands[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object WfsDB {

  def build[F[_]: Monad](
    server:        EpicsServer[F],
    top:           String,
    fluxName:      String = "dc:fgDiag1PW.VALQ",
    centroidName:  String = "dc:fgDiag1PW.VALB",
    gainResetName: String = "dc:initSigInit.J",
    telltaleName:  String = "health.VAL"
  ): Resource[F, WfsDB[F]] = for {
    sta <- WfsStatus.build(server, top, telltaleName, fluxName, centroidName)
    cmd <- WfsCommands.build[F](server, top, gainResetName)
  } yield new WfsDB[F] {
    override val status: WfsStatus[F]     = sta
    override val commands: WfsCommands[F] = cmd

    override def process: Resource[F, List[Stream[F, Unit]]] =
      commands.cads.map(_.process).sequence.map(_.flatten)

    override def clean: F[Unit] = commands.cads.map(_.clean).sequence.void
  }

}
