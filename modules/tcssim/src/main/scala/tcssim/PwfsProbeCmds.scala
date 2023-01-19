// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait PwfsProbeCmds[F[_]] {
  val park: CadRecord[F]
  val datum: CadRecord[F]
  val flip: CadRecord[F]
  val stop: CadRecord[F]
  val unwrap: CadRecord[F]
  val move: CadRecord3[F]
  val filter: AGMechanismCmds[F]
  val fieldStop: AGMechanismCmds[F]
  val focusPark: CadRecord[F]
  val focusDatum: CadRecord[F]
}

object PwfsProbeCmds {
  val ParkSuffix: String      = "Park"
  val DatumSuffix: String     = "Datum"
  val FlipSuffix: String      = "Flip"
  val StopSuffix: String      = "Stop"
  val UnwrapSuffix: String    = "Unwrap"
  val MoveSuffix: String      = "Move"
  val FilterSuffix: String    = "Filter"
  val FieldStopSuffix: String = "Fldstop"
  val FocusName: String       = "Focus"

  final case class PwfsProbeCmdsImpl[F[_]] private (
    park:       CadRecord[F],
    datum:      CadRecord[F],
    flip:       CadRecord[F],
    stop:       CadRecord[F],
    unwrap:     CadRecord[F],
    move:       CadRecord3[F],
    filter:     AGMechanismCmds[F],
    fieldStop:  AGMechanismCmds[F],
    focusPark:  CadRecord[F],
    focusDatum: CadRecord[F]
  ) extends PwfsProbeCmds[F]

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, PwfsProbeCmds[F]] = for {
    park       <- CadRecord.build(server, name + ParkSuffix)
    datum      <- CadRecord.build(server, name + DatumSuffix)
    flip       <- CadRecord.build(server, name + FlipSuffix)
    stop       <- CadRecord.build(server, name + StopSuffix)
    unwrap     <- CadRecord.build(server, name + UnwrapSuffix)
    move       <- CadRecord3.build(server, name + MoveSuffix)
    filter     <- AGMechanismCmds.build(server, name + FilterSuffix)
    fieldstop  <- AGMechanismCmds.build(server, name + FieldStopSuffix)
    focuspark  <- CadRecord.build(server, name + FocusName + ParkSuffix)
    focusdatum <- CadRecord.build(server, name + FocusName + DatumSuffix)
  } yield PwfsProbeCmdsImpl(park,
                            datum,
                            flip,
                            stop,
                            unwrap,
                            move,
                            filter,
                            fieldstop,
                            focuspark,
                            focusdatum
  )
}
