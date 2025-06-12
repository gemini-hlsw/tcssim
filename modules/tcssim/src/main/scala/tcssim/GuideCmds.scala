// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait GuideCmds[F[_]] {
  val m1Guide: CadRecord1[F]
  val m1GuideConfig: CadRecord4[F]
  val mountGuide: CadRecord4[F]
  val crGuide: CadRecord1[F]
  val m2Guide: CadRecord1[F]
  val m2GuideMode: CadRecord2[F]
  val m2GuideConfig: CadRecord7[F]
  val m2GuideReset: CadRecord[F]
  val probeGuideMode: CadRecord4[F]

  def cads: List[CadRecord[F]]
}

object GuideCmds {
  val M1GuideSuffix: String       = "m1GuideMode"
  val M1GuideConfigSuffix: String = "m1GuideConfig"
  val MountGuideSuffix: String    = "mountGuideMode"
  val CrGuideSuffix: String       = "rotGuideMode"
  val M2GuideSuffix: String       = "m2GuideControl"
  val M2GuideModeSuffix: String   = "m2GuideMode"
  val M2GuideConfigSuffix: String = "m2GuideConfig"
  val M2GuideResetSuffix: String  = "m2GuideReset"
  val ProbeGuideModeName: String  = "wfsGuideMode"

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, GuideCmds[F]] =
    for {
      m1guide       <- CadRecord1.build(server, top + M1GuideSuffix)
      m1guideconfig <- CadRecord4.build(server, top + M1GuideConfigSuffix)
      mountguide    <- CadRecord4.build(server, top + MountGuideSuffix)
      crguide       <- CadRecord1.build(server, top + CrGuideSuffix)
      m2guide       <- CadRecord1.build(server, top + M2GuideSuffix)
      m2guidemode   <- CadRecord2.build(server, top + M2GuideModeSuffix)
      m2guideconfig <- CadRecord7.build(server, top + M2GuideConfigSuffix)
      m2guidereset  <- CadRecord.build(server, top + M2GuideResetSuffix)
      probeguide    <- CadRecord4.build(server, top + ProbeGuideModeName)
    } yield new GuideCmds {
      override val m1Guide: CadRecord1[F]        = m1guide
      override val m1GuideConfig: CadRecord4[F]  = m1guideconfig
      override val mountGuide: CadRecord4[F]     = mountguide
      override val crGuide: CadRecord1[F]        = crguide
      override val m2Guide: CadRecord1[F]        = m2guide
      override val m2GuideMode: CadRecord2[F]    = m2guidemode
      override val m2GuideConfig: CadRecord7[F]  = m2guideconfig
      override val m2GuideReset: CadRecord[F]    = m2guidereset
      override val probeGuideMode: CadRecord4[F] = probeguide

      override def cads: List[CadRecord[F]] = List(
        m1Guide,
        m1GuideConfig,
        mountGuide,
        crGuide,
        m2Guide,
        m2GuideMode,
        m2GuideConfig,
        m2GuideReset,
        probeGuideMode
      )
    }
}
