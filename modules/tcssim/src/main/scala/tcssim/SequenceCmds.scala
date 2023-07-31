// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait SequenceCmds[F[_]] {
  val verify: CadRecord[F]
  val test: CadRecord[F]
  val observe: CadRecord[F]
  val endObserve: CadRecord[F]
  val guide: CadRecord[F]
  val endGuide: CadRecord[F]
  val pause: CadRecord[F]
  val continue: CadRecord[F]
}

object SequenceCmds {
  val VerifySuffix: String     = "verify"
  val TestSuffix: String       = "test"
  val ObserveSuffix: String    = "observe"
  val EndObserveSuffix: String = "endObserve"
  val GuideSuffix: String      = "guide"
  val EndGuideSuffix: String   = "endGuide"
  val PauseSuffix: String      = "pause"
  val ContinueSuffix: String   = "continue"

  case class SequenceCmdsImpl[F[_]](
    verify:     CadRecord[F],
    test:       CadRecord[F],
    observe:    CadRecord[F],
    endObserve: CadRecord[F],
    guide:      CadRecord[F],
    endGuide:   CadRecord[F],
    pause:      CadRecord[F],
    continue:   CadRecord[F]
  ) extends SequenceCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, SequenceCmds[F]] = for {
    verify     <- CadRecord.build(server, top + VerifySuffix)
    test       <- CadRecord.build(server, top + TestSuffix)
    observe    <- CadRecord.build(server, top + ObserveSuffix)
    endobserve <- CadRecord.build(server, top + EndObserveSuffix)
    guide      <- CadRecord.build(server, top + GuideSuffix)
    endguide   <- CadRecord.build(server, top + EndGuideSuffix)
    pause      <- CadRecord.build(server, top + PauseSuffix)
    continue   <- CadRecord.build(server, top + ContinueSuffix)
  } yield SequenceCmdsImpl(verify, test, observe, endobserve, guide, endguide, pause, continue)
}
