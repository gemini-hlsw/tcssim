// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait NodChopCmds[F[_]] {
  val nod: CadRecord1[F]
  val chop: CadRecord2[F]
  val beam: CadRecord1[F]

  def cads: List[CadRecord[F]]
}

object NodChopCmds {
  val NodName: String  = "nod"
  val ChopName: String = "chop"
  val BeamName: String = "m2Beam"

  private case class NodChopCmdsImpl[F[_]](
    nod:  CadRecord1[F],
    chop: CadRecord2[F],
    beam: CadRecord1[F]
  ) extends NodChopCmds[F]:
    override def cads: List[CadRecord[F]] =
      List(
        nod,
        chop,
        beam
      )

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, NodChopCmds[F]] =
    for {
      nod  <- CadRecord1.build(server, top + NodName)
      chop <- CadRecord2.build(server, top + ChopName)
      beam <- CadRecord1.build(server, top + BeamName)
    } yield NodChopCmdsImpl(nod, chop, beam)

}
