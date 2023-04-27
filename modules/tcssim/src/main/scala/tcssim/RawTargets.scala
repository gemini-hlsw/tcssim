// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV }

trait RawTargets[F[_]] {
  val targetA: MemoryPV[F, Double]
  val targetB: MemoryPV[F, Double]
  val targetC: MemoryPV[F, Double]
  val targetPwfs1: MemoryPV[F, Double]
  val targetPwfs2: MemoryPV[F, Double]
  val targetOiwfs: MemoryPV[F, Double]
  val targetG1: MemoryPV[F, Double]
  val targetG2: MemoryPV[F, Double]
  val targetG3: MemoryPV[F, Double]
  val targetG4: MemoryPV[F, Double]
}

object RawTargets {
  val TargetASuffix: String     = "targetA.VAL"
  val TargetBSuffix: String     = "targetB.VAL"
  val TargetCSuffix: String     = "targetC.VAL"
  val TargetPwfs1Suffix: String = "targetPwfs1.VAL"
  val TargetPwfs2Suffix: String = "targetPwfs2.VAL"
  val TargetOiwfsSuffix: String = "targetOiwfs.VAL"
  val TargetG1Suffix: String    = "targetG1.VAL"
  val TargetG2Suffix: String    = "targetG2.VAL"
  val TargetG3Suffix: String    = "targetG3.VAL"
  val TargetG4Suffix: String    = "targetG4.VAL"
  val TargetLength: Int         = 8

  final case class RawTargetsImpl[F[_]] private (
    targetA:     MemoryPV[F, Double],
    targetB:     MemoryPV[F, Double],
    targetC:     MemoryPV[F, Double],
    targetPwfs1: MemoryPV[F, Double],
    targetPwfs2: MemoryPV[F, Double],
    targetOiwfs: MemoryPV[F, Double],
    targetG1:    MemoryPV[F, Double],
    targetG2:    MemoryPV[F, Double],
    targetG3:    MemoryPV[F, Double],
    targetG4:    MemoryPV[F, Double]
  ) extends RawTargets[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, RawTargets[F]] = for {
    a  <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    b  <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    c  <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    p1 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    p2 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    oi <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    g1 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    g2 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    g3 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
    g4 <- server.createPV(prefix + TargetASuffix, List.fill(TargetLength)(0.0).toArray)
  } yield RawTargetsImpl(a, b, c, p1, p2, oi, g1, g2, g3, g4)
}
