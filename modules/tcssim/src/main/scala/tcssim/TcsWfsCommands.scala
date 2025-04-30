// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer

import TcsWfsCommands.BaseWfsCommands

trait TcsWfsCommands[F[_]] {
  val pwfs1: BaseWfsCommands[F]
  val pwfs2: BaseWfsCommands[F]
  val oiwfs: BaseWfsCommands[F]
  def cads: List[CadRecord[F]] = pwfs1.cads ++ pwfs2.cads ++ oiwfs.cads
}

object TcsWfsCommands {
  val ObserveCadName: String    = "Observe"
  val StopCadName: String       = "StopObserve"
  val SignalProcCadName: String = "DetSigInit"
  val DarkCadName: String       = "SeqDark"
  val ClosedLoopCadName: String = "Seq"
  val P1Prefix: String          = "pwfs1"
  val P2Prefix: String          = "pwfs2"
  val OiPrefix: String          = "oiwfs"
  val P1Short: String           = "p1"
  val P2Short: String           = "p2"
  val OiShort: String           = "oi"

  case class BaseWfsCommands[F[_]](
    observe:    CadRecord7[F],
    stop:       CadRecord[F],
    signalProc: CadRecord1[F],
    dark:       CadRecord1[F],
    closedLoop: CadRecord4[F]
  ) {
    val cads: List[CadRecord[F]] = List(observe, stop, signalProc, dark, closedLoop)
  }

  private case class TcsWfsCommandsImpl[F[_]: Monad](
    pwfs1: BaseWfsCommands[F],
    pwfs2: BaseWfsCommands[F],
    oiwfs: BaseWfsCommands[F]
  ) extends TcsWfsCommands[F]

  def buildWfsCommands[F[_]: Monad](
    server: EpicsServer[F],
    top:    String,
    wfsl:   String,
    wfss:   String
  ): Resource[F, BaseWfsCommands[F]] = for {
    ob <- CadRecord7.build(server, top + wfsl + ObserveCadName)
    st <- CadRecord.build(server, top + wfsl + StopCadName)
    sg <- CadRecord1.build(server, top + wfss + SignalProcCadName)
    dk <- CadRecord1.build(server, top + wfss + DarkCadName)
    cl <- CadRecord4.build(server, top + wfss + ClosedLoopCadName)
  } yield BaseWfsCommands(ob, st, sg, dk, cl)

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, TcsWfsCommands[F]] =
    for {
      p1 <- buildWfsCommands(server, top, P1Prefix, P1Short)
      p2 <- buildWfsCommands(server, top, P2Prefix, P2Short)
      oi <- buildWfsCommands(server, top, OiPrefix, OiShort)
    } yield TcsWfsCommandsImpl(p1, p2, oi)
}
