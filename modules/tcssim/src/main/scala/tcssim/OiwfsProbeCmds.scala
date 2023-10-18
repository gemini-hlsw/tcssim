// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer

trait OiwfsProbeCmds[F[_]] {
  val park: CadRecord[F]
  val datum: CadRecord[F]
  val filter: AGMechanismCmds[F]
  val fieldStop: AGMechanismCmds[F]
  val focusPark: CadRecord[F]
  val focusDatum: CadRecord[F]

  def cads: List[CadRecord[F]]
}

object OiwfsProbeCmds {
  val ParkSuffix: String      = "Park"
  val DatumSuffix: String     = "Datum"
  val FilterSuffix: String    = "Filter"
  val FieldStopSuffix: String = "Fldstop"
  val FocusName: String       = "Focus"

  private case class OiwfsProbeCmdsImpl[F[_]](
    park:       CadRecord[F],
    datum:      CadRecord[F],
    filter:     AGMechanismCmds[F],
    fieldStop:  AGMechanismCmds[F],
    focusPark:  CadRecord[F],
    focusDatum: CadRecord[F]
  ) extends OiwfsProbeCmds[F] {
    override def cads: List[CadRecord[F]] =
      List(
        park,
        datum,
        focusPark,
        focusDatum
      ) ++ List(filter.cads, fieldStop.cads).flatten

  }

  def build[F[_]: Applicative](
    server: EpicsServer[F],
    name:   String
  ): Resource[F, OiwfsProbeCmds[F]] = for {
    park       <- CadRecord.build(server, name + ParkSuffix)
    datum      <- CadRecord.build(server, name + DatumSuffix)
    filter     <- AGMechanismCmds.build(server, name + FilterSuffix)
    fieldstop  <- AGMechanismCmds.build(server, name + FieldStopSuffix)
    focuspark  <- CadRecord.build(server, name + FocusName + ParkSuffix)
    focusdatum <- CadRecord.build(server, name + FocusName + DatumSuffix)
  } yield OiwfsProbeCmdsImpl(park, datum, filter, fieldstop, focuspark, focusdatum)
}
