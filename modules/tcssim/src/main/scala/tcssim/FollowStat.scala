// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.BinaryEnabledDisabled._
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import epics._

trait FollowStat[F[_]] {
  val rotator: MemoryPV1[F, Int]
  val ngs1: MemoryPV1[F, BinaryEnabledDisabled]
  val ngs2: MemoryPV1[F, BinaryEnabledDisabled]
  val ngs3: MemoryPV1[F, BinaryEnabledDisabled]
  val odgw1: MemoryPV1[F, BinaryEnabledDisabled]
  val odgw2: MemoryPV1[F, BinaryEnabledDisabled]
  val odgw3: MemoryPV1[F, BinaryEnabledDisabled]
  val odgw4: MemoryPV1[F, BinaryEnabledDisabled]
}

object FollowStat {
  val RotatorSuffix: String = "crFollow.VAL"
  val Ngs1Suffix: String    = "ngsPr1FollowStat.VAL"
  val Ngs2Suffix: String    = "ngsPr2FollowStat.VAL"
  val Ngs3Suffix: String    = "ngsPr3FollowStat.VAL"
  val Odgw1Suffix: String   = "odgw1FollowStat.VAL"
  val Odgw2Suffix: String   = "odgw2FollowStat.VAL"
  val Odgw3Suffix: String   = "odgw3FollowStat.VAL"
  val Odgw4Suffix: String   = "odgw4FollowStat.VAL"

  final case class FollowStatImpl[F[_]] private (
    rotator: MemoryPV1[F, Int],
    ngs1:    MemoryPV1[F, BinaryEnabledDisabled],
    ngs2:    MemoryPV1[F, BinaryEnabledDisabled],
    ngs3:    MemoryPV1[F, BinaryEnabledDisabled],
    odgw1:   MemoryPV1[F, BinaryEnabledDisabled],
    odgw2:   MemoryPV1[F, BinaryEnabledDisabled],
    odgw3:   MemoryPV1[F, BinaryEnabledDisabled],
    odgw4:   MemoryPV1[F, BinaryEnabledDisabled]
  ) extends FollowStat[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, FollowStat[F]] = for {
    cr    <- server.createPV1(top + RotatorSuffix, 0)
    ngs1  <- server.createPV1[BinaryEnabledDisabled](top + Ngs1Suffix, Disabled)
    ngs2  <- server.createPV1[BinaryEnabledDisabled](top + Ngs2Suffix, Disabled)
    ngs3  <- server.createPV1[BinaryEnabledDisabled](top + Ngs3Suffix, Disabled)
    odgw1 <- server.createPV1[BinaryEnabledDisabled](top + Odgw1Suffix, Disabled)
    odgw2 <- server.createPV1[BinaryEnabledDisabled](top + Odgw2Suffix, Disabled)
    odgw3 <- server.createPV1[BinaryEnabledDisabled](top + Odgw3Suffix, Disabled)
    odgw4 <- server.createPV1[BinaryEnabledDisabled](top + Odgw4Suffix, Disabled)
  } yield FollowStatImpl(cr, ngs1, ngs2, ngs3, odgw1, odgw2, odgw3, odgw4)

}
