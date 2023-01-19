// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait FollowCmds[F[_]] {
  val mount: CadRecord1[F]
  val rotator: CadRecord1[F]
  val pwfs1: CadRecord1[F]
  val pwfs2: CadRecord1[F]
  val oiwfs: CadRecord1[F]
  val ao: CadRecord1[F]
  val ngs1: CadRecord1[F]
  val ngs2: CadRecord1[F]
  val ngs3: CadRecord1[F]
  val odgw1: CadRecord1[F]
  val odgw2: CadRecord1[F]
  val odgw3: CadRecord1[F]
  val odgw4: CadRecord1[F]
}

object FollowCmds {
  val MountSuffix: String   = "mcFollow"
  val RotatorSuffix: String = "crFollow"
  val Pwfs1Suffix: String   = "pwfs1Follow"
  val Pwfs2Suffix: String   = "pwfs2Follow"
  val OiwfsSuffix: String   = "oiwfsFollow"
  val AoSuffix: String      = "aoFollow"
  val Ngs1Suffix: String    = "ngs1Follow"
  val Ngs2Suffix: String    = "ngs2Follow"
  val Ngs3Suffix: String    = "ngs3Follow"
  val Odgw1Suffix: String   = "odgw1Follow"
  val Odgw2Suffix: String   = "odgw2Follow"
  val Odgw3Suffix: String   = "odgw3Follow"
  val Odgw4Suffix: String   = "odgw4Follow"

  final case class FollowCmdsImpl[F[_]] private (
    mount:   CadRecord1[F],
    rotator: CadRecord1[F],
    pwfs1:   CadRecord1[F],
    pwfs2:   CadRecord1[F],
    oiwfs:   CadRecord1[F],
    ao:      CadRecord1[F],
    ngs1:    CadRecord1[F],
    ngs2:    CadRecord1[F],
    ngs3:    CadRecord1[F],
    odgw1:   CadRecord1[F],
    odgw2:   CadRecord1[F],
    odgw3:   CadRecord1[F],
    odgw4:   CadRecord1[F]
  ) extends FollowCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, FollowCmds[F]] = for {
    mnt   <- CadRecord1.build(server, top + MountSuffix)
    cr    <- CadRecord1.build(server, top + RotatorSuffix)
    pwfs1 <- CadRecord1.build(server, top + Pwfs1Suffix)
    pwfs2 <- CadRecord1.build(server, top + Pwfs2Suffix)
    oiwfs <- CadRecord1.build(server, top + OiwfsSuffix)
    ao    <- CadRecord1.build(server, top + AoSuffix)
    ngs1  <- CadRecord1.build(server, top + Ngs1Suffix)
    ngs2  <- CadRecord1.build(server, top + Ngs2Suffix)
    ngs3  <- CadRecord1.build(server, top + Ngs3Suffix)
    odgw1 <- CadRecord1.build(server, top + Odgw1Suffix)
    odgw2 <- CadRecord1.build(server, top + Odgw2Suffix)
    odgw3 <- CadRecord1.build(server, top + Odgw3Suffix)
    odgw4 <- CadRecord1.build(server, top + Odgw4Suffix)
  } yield FollowCmdsImpl(mnt,
                         cr,
                         pwfs1,
                         pwfs2,
                         oiwfs,
                         ao,
                         ngs1,
                         ngs2,
                         ngs3,
                         odgw1,
                         odgw2,
                         odgw3,
                         odgw4
  )
}
