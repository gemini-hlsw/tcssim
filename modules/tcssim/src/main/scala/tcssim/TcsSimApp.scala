// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import cats.effect.std.Dispatcher
import cats.implicits.catsSyntaxEq
import cats.syntax.all.*
import fs2.Stream
import mouse.boolean.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import tcssim.behavior.Behavior
import tcssim.behavior.GuiderBehavior
import tcssim.behavior.TargetBehavior
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1

import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

object TcsSimApp extends IOApp {

  private implicit def L: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("tcssim")

  override def run(args: List[String]): IO[ExitCode] = {
    val r = for {
      _    <- Resource.eval(printBanner)
      dsp  <- Dispatcher.parallel[IO]
      srv  <- EpicsServer.start[IO](dsp)
      tcs  <- TcsEpicsDB.build(srv, "tc1:")
      ag   <- AGEpicsDB.build(srv, "ag:")
      crcs <- BaseSystemDB.build(srv, "cr:")
      mcs  <- BaseSystemDB.build(srv, "mc:")
      scs  <- BaseSystemDB.build(srv, "m2:")
      p1   <- WfsDB.build(
                srv,
                "pwfs1:",
                "dc:fgDiag6P1.VALH",
                "dc:fgDiag1P1.VALB"
              )
      p2   <- WfsDB.build(
                srv,
                "pwfs2:",
                "dc:fgDiag1P2.VALQ",
                "dc:fgDiag1P2.VALB"
              )
      oi   <- WfsDB.build(
                srv,
                "oiwfs:",
                "dc:fgDiag1P2.VALQ",
                "dc:fgDiag1P2.VALB",
                "dc:initSigInitFgGain.PROC",
                "dc:seeing.VAL"
              )
      ac   <- AcDB.build(srv, "hrwfs:")
      ret  <- List(
                tcs.process,
                tcs.commands.apply.DIR.valueStream
                  .map(_.evalMap(_.map(carActivity(tcs)).getOrElse(IO.unit)))
                  .map(List(_)),
                fullInpositionActivity(tcs, mcs, crcs).map(List(_)),
                ag.process,
                crcs.process,
                mcs.process,
                scs.process,
                p1.process,
                p2.process,
                oi.process,
                ac.process
              ).sequence
    } yield ret.flatten

    r.use(
      Stream.emits[IO, Stream[IO, Unit]](_).parJoinUnbounded.compile.drain
    ).as(ExitCode.Success)
  }

  val BusyTime: FiniteDuration = 1.seconds

  def carActivity(db: TcsEpicsDB[IO])(dir: CadDirective): IO[Unit] =
    if (dir === CadDirective.START)
      for {
        clid <- db.commands.apply.CLID.getOption.map(_.getOrElse(0))
        _    <- db.commands.apply.CLID.put(clid + 1)
        _    <- db.commands.apply.VAL.put(clid + 1)
        _    <- db.commands.apply.MESS.put("")
        _    <- db.commands.car.CLID.put(clid + 1)
        _    <- db.commands.car.OMSS.put("")
        _    <- db.commands.car.VAL.put(CarState.BUSY)
        _    <- runBehaviors(db)
        _    <- db.clean
        _    <- IO.sleep(BusyTime)
        _    <- db.commands.car.VAL.put(CarState.IDLE)
      } yield ()
    else IO.unit

  def singleFollowActivity(
    str:    Stream[IO, Boolean],
    status: MemoryPV1[IO, String]
  ): Stream[IO, Boolean] =
    str.flatTap(v => Stream.eval(status.put(v.fold("On", "Off"))))

  def allFollowActivity(
    db:   TcsEpicsDB[IO],
    mcs:  BaseSystemDB[IO],
    crcs: BaseSystemDB[IO]
  ): Resource[IO, Stream[IO, Unit]] = for {
    mnt <- db.commands.followCmds.mount.inputA.valueStream
    crs <- db.commands.followCmds.rotator.inputA.valueStream
  } yield Stream
    .emits(
      List(
        singleFollowActivity(mnt.map(_.exists(_.toUpperCase === "ON")), mcs.status.followS),
        singleFollowActivity(crs.map(_.exists(_.toUpperCase === "ON")), crcs.status.followS)
      )
    )
    .parJoinUnbounded
    .void

  def fullInpositionActivity(
    db:   TcsEpicsDB[IO],
    mcs:  BaseSystemDB[IO],
    crcs: BaseSystemDB[IO]
  ): Resource[IO, Stream[IO, Unit]] =
    allFollowActivity(db, mcs, crcs).map(
      _.flatMap(_ =>
        Stream.eval(
          List(mcs.status.followS.getOption, crcs.status.followS.getOption).sequence
            .flatMap(v =>
              db.status.inPosition.put(
                v.exists(_.exists(_.toUpperCase === "ON")).fold("TRUE", "FALSE")
              )
            )
        )
      )
    )

  def printBanner[F[_]: Logger]: F[Unit] = {
    val banner = """
  ______             _____ _
 /_  __/_________   / ___/(_)___ ___
  / / / ___/ ___/   \__ \/ / __ `__ \
 / / / /__(__  )   ___/ / / / / / / /
/_/  \___/____/   /____/_/_/ /_/ /_/

"""
    Logger[F].info(banner)
  }

  private val behaviors: List[Behavior[IO]] =
    TargetBehavior.allTargets[IO] :+ GuiderBehavior.behavior[IO]

  private def runBehaviors(db: TcsEpicsDB[IO]): IO[Unit] =
    behaviors.map(_.process(db)).parSequence.void

}
