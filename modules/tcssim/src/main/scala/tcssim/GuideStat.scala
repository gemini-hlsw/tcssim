// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import BinaryOnOff.*
import BinaryYesNo.*

trait GuideStat[F[_]] {
  val pwfs1: MemoryPV1[F, String]
  val pwfs2: MemoryPV1[F, String]
  val oiwfs: MemoryPV1[F, String]
  val aowfs: MemoryPV1[F, String]
  val comaCorrect: MemoryPV1[F, BinaryOnOff]
  val m1GuideConfig: MemoryPV1[F, String]
  val absorbTipTilt: MemoryPV1[F, Int]
  val m1GuideState: MemoryPV1[F, BinaryOnOff]
  val m2GuideState: MemoryPV1[F, BinaryOnOff]
  val useAo: MemoryPV1[F, BinaryYesNo]
  val p1Integrating: MemoryPV1[F, BinaryYesNo]
  val p2Integrating: MemoryPV1[F, BinaryYesNo]
  val oiIntegrating: MemoryPV1[F, BinaryYesNo]
  val p1ProbeGuide: MemoryPV1[F, Double]
  val p2ProbeGuide: MemoryPV1[F, Double]
  val oiProbeGuide: MemoryPV1[F, Double]
  val p1ProbeGuided: MemoryPV1[F, Double]
  val p2ProbeGuided: MemoryPV1[F, Double]
  val oiProbeGuided: MemoryPV1[F, Double]
  val mountP1Weight: MemoryPV1[F, Double]
  val mountP2Weight: MemoryPV1[F, Double]
}

object GuideStat {
  private val P1IntegratingName: String = "drives:p1Integrating.VAL"
  private val P2IntegratingName: String = "drives:p2Integrating.VAL"
  private val OiIntegratingName: String = "drives:oiIntegrating.VAL"
  private val M2GuideStateName: String  = "om:m2GuideState.VAL"
  private val AbsorbTipTiltName: String = "absorbTipTiltFlag.VAL"
  private val ComaCorrectName: String   = "im:m2gm:comaCorrectFlag.VAL"
  private val M1GuideStateName: String  = "im:m1GuideOn.VAL"
  private val M1GuideConfigName: String = "m1GuideConfig.VALB"

  private val P1ProbeGuideName: String  = "ak:wfsguide:p1weight.VAL"
  private val P2ProbeGuideName: String  = "ak:wfsguide:p2weight.VAL"
  private val OiProbeGuideName: String  = "ak:wfsguide:oiweight.VAL"
  private val P1ProbeGuidedName: String = "wfsGuideMode:p1.VAL"
  private val P2ProbeGuidedName: String = "wfsGuideMode:p2.VAL"
  private val OiProbeGuidedName: String = "wfsGuideMode:oi.VAL"
  private val P1MountWeightName: String = "mountGuideMode.VALC"
  private val P2MountWeightName: String = "mountGuideMode.VALD"

  private val Pwfs1Name: String = "drives:p1GuideConfig.VAL"
  private val Pwfs2Name: String = "drives:p2GuideConfig.VAL"
  private val OiwfsName: String = "drives:oiGuideConfig.VAL"
  private val AowfsName: String = "drives:aoGuideConfig.VAL"
  private val UseAoName: String = "im:AOConfigFlag.VAL"

  private case class GuideStatImpl[F[_]](
    pwfs1:         MemoryPV1[F, String],
    pwfs2:         MemoryPV1[F, String],
    oiwfs:         MemoryPV1[F, String],
    aowfs:         MemoryPV1[F, String],
    comaCorrect:   MemoryPV1[F, BinaryOnOff],
    m1GuideConfig: MemoryPV1[F, String],
    absorbTipTilt: MemoryPV1[F, Int],
    m1GuideState:  MemoryPV1[F, BinaryOnOff],
    m2GuideState:  MemoryPV1[F, BinaryOnOff],
    useAo:         MemoryPV1[F, BinaryYesNo],
    p1Integrating: MemoryPV1[F, BinaryYesNo],
    p2Integrating: MemoryPV1[F, BinaryYesNo],
    oiIntegrating: MemoryPV1[F, BinaryYesNo],
    p1ProbeGuide:  MemoryPV1[F, Double],
    p2ProbeGuide:  MemoryPV1[F, Double],
    oiProbeGuide:  MemoryPV1[F, Double],
    p1ProbeGuided: MemoryPV1[F, Double],
    p2ProbeGuided: MemoryPV1[F, Double],
    oiProbeGuided: MemoryPV1[F, Double],
    mountP1Weight: MemoryPV1[F, Double],
    mountP2Weight: MemoryPV1[F, Double]
  ) extends GuideStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, GuideStat[F]] = for {
    pwfs1 <- server.createPV1(top + Pwfs1Name, "OFF")
    pwfs2 <- server.createPV1(top + Pwfs2Name, "OFF")
    oiwfs <- server.createPV1(top + OiwfsName, "OFF")
    aowfs <- server.createPV1(top + AowfsName, "OFF")
    cc    <- server.createPV1[BinaryOnOff](top + ComaCorrectName, Off)
    m1gc  <- server.createPV1(top + M1GuideConfigName, "Off")
    att   <- server.createPV1(top + AbsorbTipTiltName, 0)
    m1gs  <- server.createPV1[BinaryOnOff](top + M1GuideStateName, Off)
    m2gs  <- server.createPV1[BinaryOnOff](top + M2GuideStateName, Off)
    usao  <- server.createPV1[BinaryYesNo](top + UseAoName, No)
    p1i   <- server.createPV1[BinaryYesNo](top + P1IntegratingName, No)
    p2i   <- server.createPV1[BinaryYesNo](top + P2IntegratingName, No)
    oii   <- server.createPV1[BinaryYesNo](top + OiIntegratingName, No)
    p1pg  <- server.createPV1[Double](top + P1ProbeGuideName, 0.0)
    p2pg  <- server.createPV1[Double](top + P2ProbeGuideName, 0.0)
    oipg  <- server.createPV1[Double](top + OiProbeGuideName, 0.0)
    p1pd  <- server.createPV1[Double](top + P1ProbeGuidedName, 0.0)
    p2pd  <- server.createPV1[Double](top + P2ProbeGuidedName, 0.0)
    oipd  <- server.createPV1[Double](top + OiProbeGuidedName, 0.0)
    mp1w  <- server.createPV1[Double](top + P1MountWeightName, 0.0)
    mp2w  <- server.createPV1[Double](top + P2MountWeightName, 0.0)
  } yield GuideStatImpl(pwfs1,
                        pwfs2,
                        oiwfs,
                        aowfs,
                        cc,
                        m1gc,
                        att,
                        m1gs,
                        m2gs,
                        usao,
                        p1i,
                        p2i,
                        oii,
                        p1pg,
                        p2pg,
                        oipg,
                        p1pd,
                        p2pd,
                        oipd,
                        mp1w,
                        mp2w
  )
}
