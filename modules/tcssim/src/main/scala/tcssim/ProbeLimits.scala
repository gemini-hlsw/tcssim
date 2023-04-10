// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait ProbeLimits[F[_]] {
  val pwfs1: PolarLimits[F]
  val pwfs2: PolarLimits[F]
  val oiwfsRect: RectLimits[F]
  val oiwfsPolar: PolarLimits[F]
  val oiwfsType: MemoryPV1[F, Int]
  val ao: RectLimits[F]
  val odgw1: RectLimits[F]
  val odgw2: RectLimits[F]
  val odgw3: RectLimits[F]
  val odgw4: RectLimits[F]
}

object ProbeLimits {
  val Pwfs1Prefix: String          = "pwfs1"
  val Pwfs2Prefix: String          = "pwfs2"
  val OiwfsPrefix: String          = "oiwfs"
  val OiwfsLimitTypeSuffix: String = "oiwfsLimitType.VAL"
  val AoPrefix: String             = "ao"
  val Odgw1Prefix: String          = "odgw1"
  val Odgw2Prefix: String          = "odgw2"
  val Odgw3Prefix: String          = "odgw3"
  val Odgw4Prefix: String          = "odgw4"

  final case class ProbeLimitsImpl[F[_]] private (
    pwfs1:      PolarLimits[F],
    pwfs2:      PolarLimits[F],
    oiwfsRect:  RectLimits[F],
    oiwfsPolar: PolarLimits[F],
    oiwfsType:  MemoryPV1[F, Int],
    ao:         RectLimits[F],
    odgw1:      RectLimits[F],
    odgw2:      RectLimits[F],
    odgw3:      RectLimits[F],
    odgw4:      RectLimits[F]
  ) extends ProbeLimits[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, ProbeLimits[F]] = for {
    p1  <- PolarLimits.build(server, prefix + Pwfs1Prefix)
    p2  <- PolarLimits.build(server, prefix + Pwfs2Prefix)
    oir <- RectLimits.build(server, prefix + OiwfsPrefix)
    oip <- PolarLimits.build(server, prefix + OiwfsPrefix)
    oit <- server.createPV1(prefix + OiwfsLimitTypeSuffix, 0)
    ao  <- RectLimits.build(server, prefix + AoPrefix)
    od1 <- RectLimits.build(server, prefix + Odgw1Prefix)
    od2 <- RectLimits.build(server, prefix + Odgw2Prefix)
    od3 <- RectLimits.build(server, prefix + Odgw3Prefix)
    od4 <- RectLimits.build(server, prefix + Odgw4Prefix)
  } yield ProbeLimitsImpl(p1, p2, oir, oip, oit, ao, od1, od2, od3, od4)
}
