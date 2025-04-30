// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.behavior

import cats.Applicative
import cats.Monad
import cats.Parallel
import cats.syntax.all.*
import monocle.Getter
import tcssim.CadRecord12
import tcssim.Target
import tcssim.TcsEpicsDB

case class TargetBehavior[F[_]: Monad: Parallel](
  cmdGetter:    Getter[TcsEpicsDB[F], CadRecord12[F]],
  statusGetter: Getter[TcsEpicsDB[F], Target[F]]
) extends Behavior[F] {
  override def process(db: TcsEpicsDB[F]): F[Unit] = cmdGetter
    .get(db)
    .MARK
    .getOption
    .flatMap(mark =>
      List(
        cmdGetter
          .get(db)
          .inputA
          .getOption
          .flatMap(
            _.map(statusGetter.get(db).objectName.put).getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputB
          .getOption
          .flatMap(
            _.map(statusGetter.get(db).inputFrame.put).getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputC
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).ra.put)
              .getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputD
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).dec.put)
              .getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputE
          .getOption
          .flatMap(
            _.map(statusGetter.get(db).equinox.put).getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputF
          .getOption
          .flatMap(
            _.map(statusGetter.get(db).epoch.put).getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputG
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).parallax.put)
              .getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputH
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).pmRA.put)
              .getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputI
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).pmDec.put)
              .getOrElse(Applicative[F].unit)
          ),
        cmdGetter
          .get(db)
          .inputJ
          .getOption
          .flatMap(
            _.flatMap(_.toDoubleOption)
              .map(statusGetter.get(db).radialVelocity.put)
              .getOrElse(Applicative[F].unit)
          )
      ).parSequence.void.whenA(mark.exists(_ === 1))
    )
}

object TargetBehavior {

  def allTargets[F[_]: Monad: Parallel]: List[Behavior[F]] = List(
    TargetBehavior[F](Getter(_.commands.targetCmds.sourceA),
                      Getter(_.status.targets.sourceATarget)
    ),
    TargetBehavior[F](Getter(_.commands.targetCmds.sourceB),
                      Getter(_.status.targets.sourceBTarget)
    ),
    TargetBehavior[F](Getter(_.commands.targetCmds.sourceC),
                      Getter(_.status.targets.sourceCTarget)
    ),
    TargetBehavior[F](Getter(_.commands.targetCmds.pwfs1), Getter(_.status.targets.pwfs1Target)),
    TargetBehavior[F](Getter(_.commands.targetCmds.pwfs2), Getter(_.status.targets.pwfs2Target)),
    TargetBehavior[F](Getter(_.commands.targetCmds.oiwfs), Getter(_.status.targets.oiwfsTarget)),
    TargetBehavior[F](Getter(_.commands.targetCmds.g1), Getter(_.status.targets.g1Target)),
    TargetBehavior[F](Getter(_.commands.targetCmds.g2), Getter(_.status.targets.g2Target)),
    TargetBehavior[F](Getter(_.commands.targetCmds.g3), Getter(_.status.targets.g3Target)),
    TargetBehavior[F](Getter(_.commands.targetCmds.g4), Getter(_.status.targets.g4Target))
  )

}
