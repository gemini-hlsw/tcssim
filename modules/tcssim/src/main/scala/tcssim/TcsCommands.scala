// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait TcsCommands[F[_]] {
  val apply: ApplyRecord[F]
  val car: CarRecord[F]
  val wfsCmds: TcsWfsCommands[F]
  val guiderTrackCommands: GuiderTrackCommands[F]
  val offsetCmds: OffsetCmds[F]
  val guideCmds: GuideCmds[F]
  val agCmds: AGCmds[F]
  val gemsCmd: GemsCmds[F]
  val altairCmds: AltairCmds[F]
  val sequenceCmds: SequenceCmds[F]
  val targetCmds: TargetCmds[F]
  val wavelenghtCmds: WavelengthCmds[F]
  val followCmds: FollowCmds[F]
  val configCmds: ConfigCmds[F]
  val nodchopCmds: NodChopCmds[F]
  val carouselModeCmd: CadRecord1[F]
  val mountCmds: MountCmds[F]
  val rotatorCmds: RotatorCmds[F]
  val defocusCmds: DeFocusCmds[F]
  val pointingOriginCmds: PointingOriginCmds[F]
  val probeGuideCmds: ProbeGuideCmds[F]
  val poAdjust: CadRecord4[F]

  def cads: List[CadRecord[F]]
}

object TcsCommands {
  val ApplySuffix: String      = "apply"
  val CarSuffix: String        = "applyC"
  val CarouselModeName: String = "carouselMode"
  val PoAdjustName: String     = "poAdjust"

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, TcsCommands[F]] =
    for {
      app  <- ApplyRecord.build(server, top + ApplySuffix)
      carr <- CarRecord.build(server, top + CarSuffix)
      wfsc <- TcsWfsCommands.build(server, top)
      gtc  <- GuiderTrackCommands.build(server, top)
      ofc  <- OffsetCmds.build(server, top)
      gdc  <- GuideCmds.build(server, top)
      agc  <- AGCmds.build(server, top)
      gms  <- GemsCmds.build(server, top)
      aoc  <- AltairCmds.build(server, top)
      seqc <- SequenceCmds.build(server, top)
      tgsc <- TargetCmds.build(server, top)
      wvlc <- WavelengthCmds.build(server, top)
      folc <- FollowCmds.build(server, top)
      cfgc <- ConfigCmds.build(server, top)
      ncc  <- NodChopCmds.build(server, top)
      cm   <- CadRecord1.build(server, top + CarouselModeName)
      mc   <- MountCmds.build(server, top)
      rc   <- RotatorCmds.build(server, top)
      df   <- DeFocusCmds.build(server, top)
      po   <- PointingOriginCmds.build(server, top)
      pg   <- ProbeGuideCmds.build(server, top)
      pa   <- CadRecord4.build(server, top + PoAdjustName)
    } yield new TcsCommands {
      override val apply: ApplyRecord[F]                       = app
      override val car: CarRecord[F]                           = carr
      override val wfsCmds: TcsWfsCommands[F]                  = wfsc
      override val guiderTrackCommands: GuiderTrackCommands[F] = gtc
      override val offsetCmds: OffsetCmds[F]                   = ofc
      override val guideCmds: GuideCmds[F]                     = gdc
      override val agCmds: AGCmds[F]                           = agc
      override val gemsCmd: GemsCmds[F]                        = gms
      override val altairCmds: AltairCmds[F]                   = aoc
      override val sequenceCmds: SequenceCmds[F]               = seqc
      override val targetCmds: TargetCmds[F]                   = tgsc
      override val wavelenghtCmds: WavelengthCmds[F]           = wvlc
      override val followCmds: FollowCmds[F]                   = folc
      override val configCmds: ConfigCmds[F]                   = cfgc
      override val nodchopCmds: NodChopCmds[F]                 = ncc
      override val carouselModeCmd: CadRecord1[F]              = cm
      override val mountCmds: MountCmds[F]                     = mc
      override val rotatorCmds: RotatorCmds[F]                 = rc
      override val defocusCmds: DeFocusCmds[F]                 = df
      override val pointingOriginCmds: PointingOriginCmds[F]   = po
      override val probeGuideCmds: ProbeGuideCmds[F]           = pg
      override val poAdjust: CadRecord4[F]                     = pa

      override def cads: List[CadRecord[F]] = List(
        wfsCmds.cads,
        guiderTrackCommands.cads,
        offsetCmds.cads,
        guideCmds.cads,
        agCmds.cads,
        gemsCmd.cads,
        altairCmds.cads,
        sequenceCmds.cads,
        targetCmds.cads,
        wavelenghtCmds.cads,
        followCmds.cads,
        configCmds.cads,
        nodchopCmds.cads,
        mountCmds.cads,
        rotatorCmds.cads,
        defocusCmds.cads,
        pointingOriginCmds.cads,
        probeGuideCmds.cads
      ).flatten :+ carouselModeCmd :+ poAdjust

    }
}
