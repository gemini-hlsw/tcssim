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

  private case class TcsSadImpl[F[_]](
    state:                 MemoryPV1[F, String],
    health:                MemoryPV1[F, String],
    heartbeat:             MemoryPV1[F, Int],
    programID:             MemoryPV1[F, String],
    trackingLimits:        TrackLimits[F],
    targets:               AllTargets[F],
    sourceADiffRA:         MemoryPV1[F, Double],
    sourceADiffDec:        MemoryPV1[F, Double],
    demands:               Demands[F],
    currentCoords:         CurrentCoords[F],
    rotTrackFrame:         MemoryPV1[F, String],
    domeVignette:          DomeVignette[F],
    times:                 Times[F],
    astCtx:                MemoryPV[F, Double],
    telCoords:             TelescopeCoords[F],
    probeLimits:           ProbeLimits[F],
    rawTargets:            RawTargets[F],
    inPosition:            MemoryPV1[F, String],
    virtualGuidersMap:     VirtualGuidersMap[F],
    guidersTrackingConfig: NDGuidersTrackConfig[F],
    follows:               FollowStat[F],
    parAngle:              MemoryPV1[F, Double],
    agInPosCalc:           MemoryPV1[F, Double],
    altair:                AltairStat[F],
    guide:                 GuideStat[F],
    nodChopStat:           NodChopStat[F],
    agStat:                AGStat[F],
    odgwParkStat:          OdgwPark[F],
    offsetStat:            OffsetStat[F],
    instrAA:               MemoryPV1[F, Double],
    instrPA:               MemoryPV1[F, Double],
    airmass:               Airmass[F],
    m2UserOffset:          MemoryPV1[F, Double]
  ) extends TcsSad[F]

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
    inp   <- server.createPV1(top + SadPrefix + InPositionSuffix, "FALSE")
    vgm   <- VirtualGuidersMap.build(server, top)
    gtc   <- NDGuidersTrackConfig.build(server, top)
    fls   <- FollowStat.build(server, top)
    pa    <- server.createPV1(top + SadPrefix + ParAngleSuffix, 0.0)
    agip  <- server.createPV1(top + AgInPositionSuffix, 0.0)
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
  } yield TcsSadImpl(st,
                     hlt,
                     hb,
                     pid,
                     tl,
                     ts,
                     dra,
                     ddec,
                     dms,
                     ccs,
                     rtf,
                     dv,
                     t,
                     actx,
                     tcoo,
                     prl,
                     rts,
                     inp,
                     vgm,
                     gtc,
                     fls,
                     pa,
                     agip,
                     altr,
                     gdst,
                     ncst,
                     agst,
                     odprk,
                     offs,
                     iaa,
                     ipa,
                     am,
                     m2of
  )
}
