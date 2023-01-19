// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer

trait AGCmds[F[_]] {
  val scienceFold: AGMechanismCmds[F]
  val scienceFoldDatum: CadRecord[F]
  val scienceFoldPark: CadRecord[F]
  val hrwfs: AGMechanismCmds[F]
  val aoFold: AGMechanismCmds[F]
  val pwfs1Probe: PwfsProbeCmds[F]
  val pwfs2Probe: PwfsProbeCmds[F]
  val oiwfsProbe: OiwfsProbeCmds[F]
}

object AGCmds {
  val ParkSuffix: String      = "Park"
  val DatumSuffix: String     = "Datum"
  val ScienceFoldName: String = "scienceFold"
  val HrwfsName: String       = "hrwfs"
  val AoFoldName: String      = "aoFold"
  val Pwfs1Name: String       = "pwfs1"
  val Pwfs2Name: String       = "pwfs2"
  val OiwfsName: String       = "oiwfs"

  final case class AGCmdsImpl[F[_]] private (
    scienceFold:      AGMechanismCmds[F],
    scienceFoldDatum: CadRecord[F],
    scienceFoldPark:  CadRecord[F],
    hrwfs:            AGMechanismCmds[F],
    aoFold:           AGMechanismCmds[F],
    pwfs1Probe:       PwfsProbeCmds[F],
    pwfs2Probe:       PwfsProbeCmds[F],
    oiwfsProbe:       OiwfsProbeCmds[F]
  ) extends AGCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, AGCmds[F]] = for {
    sciencefold      <- AGMechanismCmds.build(server, top + ScienceFoldName)
    sciencefolddatum <- CadRecord.build(server, top + ScienceFoldName + DatumSuffix)
    sciencefoldpark  <- CadRecord.build(server, top + ScienceFoldName + ParkSuffix)
    hrwfs            <- AGMechanismCmds.build(server, top + HrwfsName)
    aofold           <- AGMechanismCmds.build(server, top + AoFoldName)
    pwfs1probe       <- PwfsProbeCmds.build(server, top + Pwfs1Name)
    pwfs2probe       <- PwfsProbeCmds.build(server, top + Pwfs2Name)
    oiwfsprobe       <- OiwfsProbeCmds.build(server, top + OiwfsName)
  } yield AGCmdsImpl(sciencefold,
                     sciencefolddatum,
                     sciencefoldpark,
                     hrwfs,
                     aofold,
                     pwfs1probe,
                     pwfs2probe,
                     oiwfsprobe
  )
}
