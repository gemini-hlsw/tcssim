// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.kernel.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer

trait AGEpicsDB[F[_]] {
  val status: AG[F]
}

object AGEpicsDB {
  private case class AGEpicsDBImpl[F[_]: Monad](
    status: AG[F]
  ) extends AGEpicsDB[F] {}

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, AGEpicsDB[F]] =
    for {
      st <- AG.build(server, top)
    } yield AGEpicsDBImpl(st)
}
