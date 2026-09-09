// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import cats.Applicative
import cats.Monad
import cats.Parallel
import cats.effect.Async
import cats.effect.Temporal
import cats.syntax.all.*
import monocle.Getter
import tcssim.BinaryOnOff
import tcssim.BinaryYesNo
import tcssim.CadRecord
import tcssim.CadRecord7
import tcssim.GuideCmds
import tcssim.GuideStat
import tcssim.TcsEpicsDB
import tcssim.TcsWfsCommands
import tcssim.epics.MemoryPV1

import concurrent.duration.{Duration, DurationInt, SECONDS}

object GuiderBehavior {
  def build[F[_]: Monad: Parallel: Temporal: Async](
    db: TcsEpicsDB[F]
  ): Behavior[F] = new Behavior[F] {
    val guideCmdGetter: Getter[TcsEpicsDB[F], GuideCmds[F]]    = Getter(_.commands.guideCmds)
    val wfsCmdGetter: Getter[TcsEpicsDB[F], TcsWfsCommands[F]] = Getter(_.commands.wfsCmds)
    val statusGetter: Getter[TcsEpicsDB[F], GuideStat[F]]      = Getter(_.status.guide)

    override def process: F[Unit] = List(
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
      guideCmdGetter
        .get(db)
        .mountGuide
        .inputA
        .getOption
        .flatMap(
          _.flatMap {
            case "On"  => 1.some
            case "Off" => 0.some
            case _     => none
          }.map(statusGetter.get(db).absorbTipTilt.put)
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
      observe:     CadRecord7[F],
      stopObserve: CadRecord[F],
      status:      MemoryPV1[F, BinaryYesNo]
    ): F[Unit] = for {
      o <- observe.MARK.getOption
      s <- stopObserve.MARK.getOption
      c <- observe.inputA.getOption.map(_.flatMap(_.toIntOption))
      t <- observe.inputB.getOption.map(_.flatMap(_.toDoubleOption))
      w  = (c, t).mapN(_.toDouble * _).filter(_ => c.exists(_ > 0))
      _ <- if (s.exists(_ === 1))
             Async[F].start(Temporal[F].sleep(6.seconds) *> status.put(BinaryYesNo.No)).void
           else if (o.exists(_ === 1))
             Async[F]
               .start(
                 Temporal[F].sleep(7.seconds) *> status.put(BinaryYesNo.Yes) *>
                   w.map(x => Temporal[F].sleep(Duration(x, SECONDS)) *> status.put(BinaryYesNo.No))
                     .getOrElse(Applicative[F].unit)
               )
               .void
           else Applicative[F].unit
    } yield ()
  }
}
