// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import BinaryOnOff._
import BinaryYesNo._

trait GuideStat[F[_]] {
  val pwfs1: MemoryPV1[F, String]
  val pwfs2: MemoryPV1[F, String]
  val oiwfs: MemoryPV1[F, String]
  val aowfs: MemoryPV1[F, String]
  val comaCorrect: MemoryPV1[F, String]
  val m1GuideConfig: MemoryPV1[F, String]
  val absorbTipTilt: MemoryPV1[F, Int]
  val m1GuideState: MemoryPV1[F, BinaryOnOff]
  val m2GuideState: MemoryPV1[F, BinaryOnOff]
  val useAo: MemoryPV1[F, BinaryYesNo]
  val p1Integrating: MemoryPV1[F, BinaryYesNo]
  val p2Integrating: MemoryPV1[F, BinaryYesNo]
  val oiIntegrating: MemoryPV1[F, BinaryYesNo]
}

object GuideStat {
  private val Pwfs1Name: String         = "drives:p1GuideConfig.VAL"
  private val Pwfs2Name: String         = "drives:p2GuideConfig.VAL"
  private val OiwfsName: String         = "drives:oiGuideConfig.VAL"
  private val AowfsName: String         = "drives:aoGuideConfig.VAL"
  private val ComaCorrectName: String   = "comaCorrect.VAL"
  private val M1GuideConfigName: String = "m1GuideConfig.VALB"
  private val AbsorbTipTiltName: String = "absorbTipTiltFlag.VAL"
  private val M1GuideStateName: String  = "im:m1GuideOn.VAL"
  private val M2GuideStateName: String  = "om:m2GuideState.VAL"
  private val UseAoName: String         = "im:AOConfigFlag.VAL"
  private val P1IntegratingName: String = "drives:p1Integrating.VAL"
  private val P2IntegratingName: String = "drives:p2Integrating.VAL"
  private val OiIntegratingName: String = "drives:oiIntegrating.VAL"

  private case class GuideStatImpl[F[_]](
    pwfs1:         MemoryPV1[F, String],
    pwfs2:         MemoryPV1[F, String],
    oiwfs:         MemoryPV1[F, String],
    aowfs:         MemoryPV1[F, String],
    comaCorrect:   MemoryPV1[F, String],
    m1GuideConfig: MemoryPV1[F, String],
    absorbTipTilt: MemoryPV1[F, Int],
    m1GuideState:  MemoryPV1[F, BinaryOnOff],
    m2GuideState:  MemoryPV1[F, BinaryOnOff],
    useAo:         MemoryPV1[F, BinaryYesNo],
    p1Integrating: MemoryPV1[F, BinaryYesNo],
    p2Integrating: MemoryPV1[F, BinaryYesNo],
    oiIntegrating: MemoryPV1[F, BinaryYesNo]
  ) extends GuideStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, GuideStat[F]] = for {
    pwfs1 <- server.createPV1(top + Pwfs1Name, "OFF")
    pwfs2 <- server.createPV1(top + Pwfs2Name, "OFF")
    oiwfs <- server.createPV1(top + OiwfsName, "OFF")
    aowfs <- server.createPV1(top + AowfsName, "OFF")
    cc    <- server.createPV1(top + ComaCorrectName, "Off")
    m1gc  <- server.createPV1(top + M1GuideConfigName, "Off")
    att   <- server.createPV1(top + AbsorbTipTiltName, 0)
    m1gs  <- server.createPV1[BinaryOnOff](top + M1GuideStateName, Off)
    m2gs  <- server.createPV1[BinaryOnOff](top + M2GuideStateName, Off)
    usao  <- server.createPV1[BinaryYesNo](top + UseAoName, No)
    p1i   <- server.createPV1[BinaryYesNo](top + P1IntegratingName, No)
    p2i   <- server.createPV1[BinaryYesNo](top + P2IntegratingName, No)
    oii   <- server.createPV1[BinaryYesNo](top + OiIntegratingName, No)
  } yield GuideStatImpl(pwfs1, pwfs2, oiwfs, aowfs, cc, m1gc, att, m1gs, m2gs, usao, p1i, p2i, oii)
}
