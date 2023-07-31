// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

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

  private case class GuideCmdsImpl[F[_]](
    m1Guide:       CadRecord1[F],
    m1GuideConfig: CadRecord4[F],
    mountGuide:    CadRecord4[F],
    crGuide:       CadRecord1[F],
    m2Guide:       CadRecord1[F],
    m2GuideMode:   CadRecord2[F],
    m2GuideConfig: CadRecord7[F],
    m2GuideReset:  CadRecord[F]
  ) extends GuideCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, GuideCmds[F]] = for {
    m1guide       <- CadRecord1.build(server, top + M1GuideSuffix)
    m1guideconfig <- CadRecord4.build(server, top + M1GuideConfigSuffix)
    mountguide    <- CadRecord4.build(server, top + MountGuideSuffix)
    crguide       <- CadRecord1.build(server, top + CrGuideSuffix)
    m2guide       <- CadRecord1.build(server, top + M2GuideSuffix)
    m2guidemode   <- CadRecord2.build(server, top + M2GuideModeSuffix)
    m2guideconfig <- CadRecord7.build(server, top + M2GuideConfigSuffix)
    m2guidereset  <- CadRecord.build(server, top + M2GuideResetSuffix)
  } yield GuideCmdsImpl(m1guide,
                        m1guideconfig,
                        mountguide,
                        crguide,
                        m2guide,
                        m2guidemode,
                        m2guideconfig,
                        m2guidereset
  )
}
