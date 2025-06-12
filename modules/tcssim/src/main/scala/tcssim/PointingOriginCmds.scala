// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait PointingOriginCmds[F[_]] {
  val instrumentOriginA: CadRecord2[F]
  val instrumentOriginB: CadRecord2[F]
  val instrumentOriginC: CadRecord2[F]

  def cads: List[CadRecord[F]]
}

object PointingOriginCmds {

  case class PointingOriginCmdsImpl[F[_]: Monad](
    instrumentOriginA: CadRecord2[F],
    instrumentOriginB: CadRecord2[F],
    instrumentOriginC: CadRecord2[F]
  ) extends PointingOriginCmds[F] {
    override def cads: List[CadRecord[F]] =
      List(instrumentOriginA, instrumentOriginB, instrumentOriginC)
  }

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, PointingOriginCmds[F]] =
    for {
      a <- CadRecord2.build(server, s"${top}poriginA")
      b <- CadRecord2.build(server, s"${top}poriginB")
      c <- CadRecord2.build(server, s"${top}poriginC")
    } yield PointingOriginCmdsImpl(a, b, c)
}
