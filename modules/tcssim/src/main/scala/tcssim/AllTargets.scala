// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait AllTargets[F[_]] {
  val sourceATarget: Target[F]
  val sourceBTarget: Target[F]
  val sourceCTarget: Target[F]
  val pwfs1Target: Target[F]
  val pwfs2Target: Target[F]
  val oiwfsTarget: Target[F]
  val g1Target: Target[F]
  val g2Target: Target[F]
  val g3Target: Target[F]
  val g4Target: Target[F]
  val mountTarget: Target[F]
  val sourceADiffFrame: MemoryPV1[F, String]
  val sourceADiffEpoch: MemoryPV1[F, Double]
  val sourceADiffRA: MemoryPV1[F, Double]
  val sourceADiffDec: MemoryPV1[F, Double]
}

object AllTargets {
  val SourceAPrefix: String        = "sourceA"
  val SourceBPrefix: String        = "sourceB"
  val SourceCPrefix: String        = "sourceC"
  val Pwfs1Prefix: String          = "pwfs1"
  val Pwfs2Prefix: String          = "pwfs2"
  val OiwfsPrefix: String          = "oiwfs"
  val G1Prefix: String             = "g1"
  val G2Prefix: String             = "g2"
  val G3Prefix: String             = "g3"
  val G4Prefix: String             = "g4"
  val MountPrefix: String          = "mount"
  val SourceADiffFrameName: String = "sourceADiffFrame.VAL"
  val SourceADiffEpochName: String = "sourceADiffEpoch.VAL"
  val SourceADiffRAName: String    = "sourceADiffRA.VAL"
  val SourceADiffDecName: String   = "sourceADiffDec.VAL"

  final case class AllTargetsImpl[F[_]] private (
    sourceATarget:    Target[F],
    sourceBTarget:    Target[F],
    sourceCTarget:    Target[F],
    pwfs1Target:      Target[F],
    pwfs2Target:      Target[F],
    oiwfsTarget:      Target[F],
    g1Target:         Target[F],
    g2Target:         Target[F],
    g3Target:         Target[F],
    g4Target:         Target[F],
    mountTarget:      Target[F],
    sourceADiffFrame: MemoryPV1[F, String],
    sourceADiffEpoch: MemoryPV1[F, Double],
    sourceADiffRA:    MemoryPV1[F, Double],
    sourceADiffDec:   MemoryPV1[F, Double]
  ) extends AllTargets[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, AllTargets[F]] = for {
    sra         <- Target.build(server, prefix + SourceAPrefix)
    srb         <- Target.build(server, prefix + SourceBPrefix)
    src         <- Target.build(server, prefix + SourceCPrefix)
    p1          <- Target.build(server, prefix + Pwfs1Prefix)
    p2          <- Target.build(server, prefix + Pwfs2Prefix)
    oi          <- Target.build(server, prefix + OiwfsPrefix)
    g1          <- Target.build(server, prefix + G1Prefix)
    g2          <- Target.build(server, prefix + G2Prefix)
    g3          <- Target.build(server, prefix + G3Prefix)
    g4          <- Target.build(server, prefix + G4Prefix)
    mnt         <- Target.build(server, prefix + MountPrefix)
    sadiffframe <- server.createPV1(prefix + SourceADiffFrameName, "")
    sadiffepoch <- server.createPV1(prefix + SourceADiffEpochName, 2000.0)
    sadiffra    <- server.createPV1(prefix + SourceADiffRAName, 0.0)
    sadiffdec   <- server.createPV1(prefix + SourceADiffDecName, 0.0)
  } yield AllTargetsImpl(sra,
                         srb,
                         src,
                         p1,
                         p2,
                         oi,
                         g1,
                         g2,
                         g3,
                         g4,
                         mnt,
                         sadiffframe,
                         sadiffepoch,
                         sadiffra,
                         sadiffdec
  )

}
