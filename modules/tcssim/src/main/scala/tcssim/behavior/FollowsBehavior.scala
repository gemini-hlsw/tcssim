// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import cats.Monad
import tcssim.TcsEpicsDB

object FollowsBehavior {
  def build[F[_]: Monad](db: TcsEpicsDB[F]): Behavior[F] = new Behavior[F] {

    override def process: F[Unit] = Monad[F].unit
    //    for {
    //      prk <- db.commands.mountCmds.park.MARK.getOption
    //      flm <- db.commands.followCmds.mount.MARK.getOption
    //      fl  <- db.commands.followCmds.mount.inputA.getOption
    //      _ <- prk.flatMap((_ === 1).fold())
    //    } yield ()
  }
}
