// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV
import tcssim.epics.MemoryPV1
import tcssim.epics.given

sealed trait TcsSad[F[_]] {
  val state: MemoryPV1[F, String]
  val health: MemoryPV1[F, String]
  val heartbeat: MemoryPV1[F, Int]
  val programID: MemoryPV1[F, String]
  val trackingLimits: TrackLimits[F]
  val targets: AllTargets[F]
  val sourceADiffRA: MemoryPV1[F, Double]
  val sourceADiffDec: MemoryPV1[F, Double]
  val demands: Demands[F]
  val currentCoords: CurrentCoords[F]
  val rotTrackFrame: MemoryPV1[F, String]
  val domeVignette: DomeVignette[F]
  val times: Times[F]
  val astCtx: MemoryPV[F, Double]
  val telCoords: TelescopeCoords[F]
  val probeLimits: ProbeLimits[F]
  val rawTargets: RawTargets[F]
  val inPosition: MemoryPV1[F, String]
  val virtualGuidersMap: VirtualGuidersMap[F]
  val guidersTrackingConfig: NDGuidersTrackConfig[F]
  val follows: FollowStat[F]
  val parAngle: MemoryPV1[F, Double]
  val agInPosCalc: MemoryPV1[F, Double]
  val altair: AltairStat[F]
  val guide: GuideStat[F]
  val nodChopStat: NodChopStat[F]
  val agStat: AGStat[F]
  val odgwParkStat: OdgwPark[F]
  val offsetStat: OffsetStat[F]
  val instrAA: MemoryPV1[F, Double]
  val instrPA: MemoryPV1[F, Double]
  val airmass: Airmass[F]
  val m2UserOffset: MemoryPV1[F, Double]
  val defocus: MemoryPV1[F, Double]
  val nodState: MemoryPV1[F, String]
  val targetA: MemoryPV[F, Double]
  val targetPwfs1: MemoryPV[F, Double]
  val targetPwfs2: MemoryPV[F, Double]
  val targetOiwfs: MemoryPV[F, Double]
  val pointingCorrections: PointingCorrectionReadout[F]
  val pwfs1Mechs: PwfsMechsCommands[F]
  val pwfs2Mechs: PwfsMechsCommands[F]
}

object TcsSad {
  val SadPrefix: String            = "sad:"
  val StateSuffix: String          = "state.VAL"
  val HealthSuffix: String         = "health.VAL"
  val HeartbeatSuffix: String      = "heartbeat.VAL"
  val ProgramIdSuffix: String      = "programID,VAL"
  val SourceADiffRASuffix: String  = "sourceADiffRA.VAL"
  val SourceADiffDecSuffix: String = "sourceADiffDec.VAL"
  val RotTrackFrameSuffix: String  = "rotTrackFrame.VAL"
  val AstCtxSuffix: String         = "astCtx.VAL"
  val CtxLength: Int               = 39
  val InPositionSuffix: String     = "inPosition.VAL"
  val ParAngleSuffix: String       = "parAngle.VAL"
  val AgInPositionSuffix: String   = "agInPosCalc.VAL"
  val InstrAASuffix: String        = "instrAA.VAL"
  val InstrPASuffix: String        = "instrPA.VAL"
  val M2UserOffsetSuffix: String   = "m2ZUserOffset.VAL"
  val DefocusSuffix: String        = "dtelFocus.VALB"
  val NodStateName: String         = "nodState.VAL"
  val TargetAName: String          = "targetA.VAL"
  val TargetPwfs1Name: String      = "targetPwfs1.VAL"
  val TargetPwfs2Name: String      = "targetPwfs2.VAL"
  val TargetOiwfsName: String      = "targetOiwfs.VAL"

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, TcsSad[F]] = for {
    st    <- server.createPV1(top + SadPrefix + StateSuffix, "RUNNING")
    hlt   <- server.createPV1(top + SadPrefix + HealthSuffix, "GOOD")
    hb    <- server.createPV1(top + SadPrefix + HeartbeatSuffix, 0)
    pid   <- server.createPV1(top + SadPrefix + ProgramIdSuffix, "")
    tl    <- TrackLimits.build(server, top + SadPrefix)
    ts    <- AllTargets.build(server, top + SadPrefix)
    dra   <- server.createPV1(top + SadPrefix + SourceADiffRASuffix, 0.0)
    ddec  <- server.createPV1(top + SadPrefix + SourceADiffDecSuffix, 0.0)
    dms   <- Demands.build(server, top + SadPrefix)
    ccs   <- CurrentCoords.build(server, top, top + SadPrefix)
    rtf   <- server.createPV1(top + SadPrefix + RotTrackFrameSuffix, "")
    dv    <- DomeVignette.build(server, top + SadPrefix)
    t     <- Times.build(server, top, top + SadPrefix)
    actx  <- server.createPV(top + SadPrefix + AstCtxSuffix, List.fill(CtxLength)(0.0).toArray)
    tcoo  <- TelescopeCoords.build(server, top + SadPrefix)
    prl   <- ProbeLimits.build(server, top + SadPrefix)
    rts   <- RawTargets.build(server, top + SadPrefix)
    inp   <- server.createPV1(top + SadPrefix + InPositionSuffix, "TRUE")
    vgm   <- VirtualGuidersMap.build(server, top)
    gtc   <- NDGuidersTrackConfig.build(server, top)
    fls   <- FollowStat.build(server, top)
    pa    <- server.createPV1(top + SadPrefix + ParAngleSuffix, 0.0)
    agip  <- server.createPV1(top + AgInPositionSuffix, 1.0)
    altr  <- AltairStat.build(server, top)
    gdst  <- GuideStat.build(server, top)
    ncst  <- NodChopStat.build(server, top, top + SadPrefix)
    agst  <- AGStat.build(server, top)
    odprk <- OdgwPark.build(server, top)
    offs  <- OffsetStat.build(server, top)
    iaa   <- server.createPV1(top + SadPrefix + InstrAASuffix, 0.0)
    ipa   <- server.createPV1(top + SadPrefix + InstrPASuffix, 0.0)
    am    <- Airmass.build(server, top + SadPrefix)
    m2of  <- server.createPV1(top + SadPrefix + M2UserOffsetSuffix, 0.0)
    df    <- server.createPV1(top + DefocusSuffix, 0.0)
    ns    <- server.createPV1(top + SadPrefix + NodStateName, "A")
    tapv  <- server.createPV(top + SadPrefix + TargetAName, Array.fill(8)(0.0))
    tp1pv <- server.createPV(top + SadPrefix + TargetPwfs1Name, Array.fill(8)(0.0))
    tp2pv <- server.createPV(top + SadPrefix + TargetPwfs2Name, Array.fill(8)(0.0))
    toipv <- server.createPV(top + SadPrefix + TargetOiwfsName, Array.fill(8)(0.0))
    pcr   <- PointingCorrectionReadout.build(server, top)
    p1    <- PwfsMechsCommands.build(server, top, "pwfs1")
    p2    <- PwfsMechsCommands.build(server, top, "pwfs2")
  } yield new TcsSad[F] {
    override val state: MemoryPV1[F, String]                       = st
    override val health: MemoryPV1[F, String]                      = hlt
    override val heartbeat: MemoryPV1[F, Int]                      = hb
    override val programID: MemoryPV1[F, String]                   = pid
    override val trackingLimits: TrackLimits[F]                    = tl
    override val targets: AllTargets[F]                            = ts
    override val sourceADiffRA: MemoryPV1[F, Double]               = dra
    override val sourceADiffDec: MemoryPV1[F, Double]              = ddec
    override val demands: Demands[F]                               = dms
    override val currentCoords: CurrentCoords[F]                   = ccs
    override val rotTrackFrame: MemoryPV1[F, String]               = rtf
    override val domeVignette: DomeVignette[F]                     = dv
    override val times: Times[F]                                   = t
    override val astCtx: MemoryPV[F, Double]                       = actx
    override val telCoords: TelescopeCoords[F]                     = tcoo
    override val probeLimits: ProbeLimits[F]                       = prl
    override val rawTargets: RawTargets[F]                         = rts
    override val inPosition: MemoryPV1[F, String]                  = inp
    override val virtualGuidersMap: VirtualGuidersMap[F]           = vgm
    override val guidersTrackingConfig: NDGuidersTrackConfig[F]    = gtc
    override val follows: FollowStat[F]                            = fls
    override val parAngle: MemoryPV1[F, Double]                    = pa
    override val agInPosCalc: MemoryPV1[F, Double]                 = agip
    override val altair: AltairStat[F]                             = altr
    override val guide: GuideStat[F]                               = gdst
    override val nodChopStat: NodChopStat[F]                       = ncst
    override val agStat: AGStat[F]                                 = agst
    override val odgwParkStat: OdgwPark[F]                         = odprk
    override val offsetStat: OffsetStat[F]                         = offs
    override val instrAA: MemoryPV1[F, Double]                     = iaa
    override val instrPA: MemoryPV1[F, Double]                     = ipa
    override val airmass: Airmass[F]                               = am
    override val m2UserOffset: MemoryPV1[F, Double]                = m2of
    override val defocus: MemoryPV1[F, Double]                     = df
    override val nodState: MemoryPV1[F, String]                    = ns
    override val targetA: MemoryPV[F, Double]                      = tapv
    override val targetPwfs1: MemoryPV[F, Double]                  = tp1pv
    override val targetPwfs2: MemoryPV[F, Double]                  = tp2pv
    override val targetOiwfs: MemoryPV[F, Double]                  = toipv
    override val pointingCorrections: PointingCorrectionReadout[F] = pcr
    override val pwfs1Mechs: PwfsMechsCommands[F]                  = p1
    override val pwfs2Mechs: PwfsMechsCommands[F]                  = p2
  }
}
