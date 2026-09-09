// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
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
  val poOffsetAbsorb: CadRecord2[F]
  val poOffsetClear: CadRecord2[F]
  val targetAdjust: CadRecord4[F]
  val targetOffsetAbsorb: CadRecord2[F]
  val targetOffsetClear: CadRecord2[F]
  val pointingAdjust: CadRecord4[F]
  val pointingConfig: CadRecord4[F]
  val absorbGuide: CadRecord[F]
  val zeroGuide: CadRecord[F]
  val instrumentOffset: CadRecord2[F]
  val azimuthWrap: CadRecord1[F]
  val rotatorWrap: CadRecord1[F]
  val zeroRotatorGuide: CadRecord[F]
  val oiwfsSelect: CadRecord2[F]
  val chopConfig: CadRecord4[F]
  val chopRelative: CadRecord4[F]
  val baffles: CadRecord2[F]
  val wfsGuideMode: CadRecord12[F]

  def cads: List[CadRecord[F]]
}

object TcsCommands {
  val ApplySuffix: String          = "apply"
  val CarSuffix: String            = "applyC"
  val CarouselModeName: String     = "carouselMode"
  val PoAdjustName: String         = "poAdjust"
  val TargetAdjustName: String     = "targetAdjust"
  val PointingAdjustName: String   = "collAdjust"
  val TargetAbsorbName: String     = "absorb"
  val TargetClearName: String      = "clear"
  val OriginAbsorbName: String     = "absorbPo"
  val OriginClearName: String      = "clearPo"
  val PointingConfigName: String   = "pointParam"
  val AbsorbGuideName: String      = "absorbGuide"
  val ZeroGuideName: String        = "zeroGuide"
  val AzWrapName: String           = "azwrap"
  val RotWrapName: String          = "rotwrap"
  val ZeroRotGuideName: String     = "zeroRotGuide"
  val InstrumentOffsetName: String = "offsetPoA1"
  val OiwfsSelectName: String      = "oiwfsSelect"
  val ChopConfigName: String       = "chopConfig"
  val ChopRelativeName: String     = "chopRelative"
  val BafflesName: String          = "m2Baffle"
  val WfsGuideModeName: String     = "wfsGuideMode"

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
      poa  <- CadRecord4.build(server, top + PoAdjustName)
      ta   <- CadRecord4.build(server, top + TargetAdjustName)
      pta  <- CadRecord4.build(server, top + PointingAdjustName)
      toa  <- CadRecord2.build(server, top + TargetAbsorbName)
      toc  <- CadRecord2.build(server, top + TargetClearName)
      pooa <- CadRecord2.build(server, top + OriginAbsorbName)
      pooc <- CadRecord2.build(server, top + OriginClearName)
      pcfg <- CadRecord4.build(server, s"${top}$PointingConfigName")
      abg  <- CadRecord.build(server, s"${top}$AbsorbGuideName")
      zrg  <- CadRecord.build(server, s"${top}$ZeroGuideName")
      inso <- CadRecord2.build(server, s"${top}$InstrumentOffsetName")
      azwr <- CadRecord1.build(server, s"${top}$AzWrapName")
      rtwr <- CadRecord1.build(server, s"${top}$RotWrapName")
      zrg  <- CadRecord.build(server, s"${top}$ZeroRotGuideName")
      ois  <- CadRecord2.build(server, s"${top}$OiwfsSelectName")
      chC  <- CadRecord4.build(server, s"${top}$ChopConfigName")
      chR  <- CadRecord4.build(server, s"${top}$ChopRelativeName")
      bf   <- CadRecord2.build(server, s"${top}$BafflesName")
      gm   <- CadRecord12.build(server, s"${top}$WfsGuideModeName")
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
      override val poAdjust: CadRecord4[F]                     = poa
      override val targetAdjust: CadRecord4[F]                 = ta
      override val pointingAdjust: CadRecord4[F]               = pta
      override val poOffsetAbsorb: CadRecord2[F]               = pooa
      override val poOffsetClear: CadRecord2[F]                = pooc
      override val targetOffsetAbsorb: CadRecord2[F]           = toa
      override val targetOffsetClear: CadRecord2[F]            = toc
      override val pointingConfig: CadRecord4[F]               = pcfg
      override val absorbGuide: CadRecord[F]                   = abg
      override val zeroGuide: CadRecord[F]                     = zrg
      override val instrumentOffset: CadRecord2[F]             = inso
      override val azimuthWrap: CadRecord1[F]                  = azwr
      override val rotatorWrap: CadRecord1[F]                  = rtwr
      override val zeroRotatorGuide: CadRecord[F]              = zrg
      override val oiwfsSelect: CadRecord2[F]                  = ois
      override val chopConfig: CadRecord4[F]                   = chC
      override val chopRelative: CadRecord4[F]                 = chR
      override val baffles: CadRecord2[F]                      = bf
      override val wfsGuideMode: CadRecord12[F]                = gm

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
      ).flatten ++ List(
        carouselModeCmd,
        poAdjust,
        targetAdjust,
        pointingAdjust,
        poOffsetAbsorb,
        poOffsetClear,
        targetOffsetAbsorb,
        targetOffsetClear,
        pointingConfig,
        absorbGuide,
        zeroGuide,
        instrumentOffset,
        azimuthWrap,
        rotatorWrap,
        zeroRotatorGuide,
        oiwfsSelect,
        chopConfig,
        chopRelative,
        baffles,
        wfsGuideMode
      )

    }
}
