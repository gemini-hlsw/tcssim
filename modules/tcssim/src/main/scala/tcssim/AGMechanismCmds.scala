// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer

trait AGMechanismCmds[F[_]] {
  val move: CadRecord1[F]
  val park: CadRecord[F]
  val datum: CadRecord[F]

  def cads: List[CadRecord[F]]
}

object AGMechanismCmds {
  val ParkSuffix: String  = "Park"
  val DatumSuffix: String = "Datum"

  private case class AGMechanismCmdsImpl[F[_]](
    move:  CadRecord1[F],
    park:  CadRecord[F],
    datum: CadRecord[F]
  ) extends AGMechanismCmds[F] {
    override def cads: List[CadRecord[F]] =
      List(
        move,
        park,
        datum
      )

  }

  def build[F[_]: Monad](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, AGMechanismCmds[F]] = for {
    move  <- CadRecord1.build(server, top)
    park  <- CadRecord.build(server, top + ParkSuffix)
    datum <- CadRecord.build(server, top + DatumSuffix)
  } yield AGMechanismCmdsImpl(move, park, datum)
}
