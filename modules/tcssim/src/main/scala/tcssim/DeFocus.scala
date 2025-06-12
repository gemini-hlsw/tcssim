// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait DeFocusCmds[F[_]] {
  val dtelFocus: CadRecord2[F]

  def cads: List[CadRecord[F]] = List(dtelFocus)
}

object DeFocusCmds {
  val DefocusSuffix: String = "dtelFocus"

  private case class DefocusCmdsImpl[F[_]](
    dtelFocus: CadRecord2[F]
  ) extends DeFocusCmds[F]

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, DeFocusCmds[F]] =
    for {
      dtelFocus <- CadRecord2.build(server, top + DefocusSuffix)
    } yield DefocusCmdsImpl[F](dtelFocus)
}
