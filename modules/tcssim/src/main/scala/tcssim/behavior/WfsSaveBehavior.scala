// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import cats.Applicative
import cats.Monad
import cats.syntax.all.*
import tcssim.WfsCommands
import tcssim.WfsStatus

object WfsSaveBehavior {
  def build[F[_]: Monad](cmd: WfsCommands[F], status: WfsStatus[F]): Behavior[F] = new Behavior[F] {
    override def process: F[Unit] = cmd.circularBuffer.MARK.getOption.flatMap { mark =>
      (cmd.circularBuffer.inputA.getOption.flatMap {
        case Some("1") => status.imgSave.put("TRUE")
        case Some("0") => status.imgSave.put("FALSE")
        case _         => Applicative[F].unit
      } *>
        cmd.circularBuffer.inputB.getOption.flatMap {
          case Some("1") => status.aoSave.put("TRUE")
          case Some("0") => status.aoSave.put("FALSE")
          case _         => Applicative[F].unit
        } *>
        cmd.circularBuffer.inputC.getOption.flatMap {
          case Some("1") => status.fgSave.put("TRUE")
          case Some("0") => status.fgSave.put("FALSE")
          case _         => Applicative[F].unit
        }).whenA(mark === Some(1))
    }
  }
}
