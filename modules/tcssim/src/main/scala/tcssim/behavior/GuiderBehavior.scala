// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import cats.Applicative
import cats.Monad
import cats.Parallel
import cats.syntax.all.*
import monocle.Getter
import tcssim.BinaryOnOff
import tcssim.BinaryYesNo
import tcssim.CadRecord
import tcssim.GuideCmds
import tcssim.GuideStat
import tcssim.TcsEpicsDB
import tcssim.TcsWfsCommands
import tcssim.epics.MemoryPV1

case class GuiderBehavior[F[_]: Monad: Parallel](
  guideCmdGetter: Getter[TcsEpicsDB[F], GuideCmds[F]],
  wfsCmdGetter:   Getter[TcsEpicsDB[F], TcsWfsCommands[F]],
  statusGetter:   Getter[TcsEpicsDB[F], GuideStat[F]]
) extends Behavior[F] {
  override def process(db: TcsEpicsDB[F]): F[Unit] = List(
    guideCmdGetter
      .get(db)
      .m2Guide
      .inputA
      .getOption
      .flatMap(
        _.flatMap {
          case "On"  => BinaryOnOff.On.some
          case "Off" => BinaryOnOff.Off.some
          case _     => none
        }.map(statusGetter.get(db).m2GuideState.put)
          .getOrElse(Applicative[F].unit)
      ),
    guideCmdGetter
      .get(db)
      .m1Guide
      .inputA
      .getOption
      .flatMap(
        _.flatMap {
          case "On"  => BinaryOnOff.On.some
          case "Off" => BinaryOnOff.Off.some
          case _     => none
        }.map(statusGetter.get(db).m1GuideState.put)
          .getOrElse(Applicative[F].unit)
      ),
    guideCmdGetter
      .get(db)
      .m1GuideConfig
      .inputB
      .getOption
      .flatMap(
        _.map(statusGetter.get(db).m1GuideConfig.put)
          .getOrElse(Applicative[F].unit)
      ),
    wfs(wfsCmdGetter.get(db).pwfs1.observe,
        wfsCmdGetter.get(db).pwfs1.stop,
        statusGetter.get(db).p1Integrating
    ),
    wfs(wfsCmdGetter.get(db).pwfs2.observe,
        wfsCmdGetter.get(db).pwfs2.stop,
        statusGetter.get(db).p2Integrating
    ),
    wfs(wfsCmdGetter.get(db).oiwfs.observe,
        wfsCmdGetter.get(db).oiwfs.stop,
        statusGetter.get(db).oiIntegrating
    )
  ).parSequence.void

  def wfs(
    observe:     CadRecord[F],
    stopObserve: CadRecord[F],
    status:      MemoryPV1[F, BinaryYesNo]
  ): F[Unit] = for {
    o <- observe.MARK.getOption
    s <- stopObserve.MARK.getOption
    _ <- if (s.exists(_ === 1)) status.put(BinaryYesNo.No)
         else if (o.exists(_ === 1)) status.put(BinaryYesNo.Yes)
         else Applicative[F].unit
  } yield ()

}

object GuiderBehavior {

  def behavior[F[_]: Monad: Parallel]: Behavior[F] =
    GuiderBehavior(Getter(_.commands.guideCmds), Getter(_.commands.wfsCmds), Getter(_.status.guide))
}
