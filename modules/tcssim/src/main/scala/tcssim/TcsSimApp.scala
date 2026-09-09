// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
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
import tcssim.behavior.GuiderBehavior
import tcssim.behavior.TargetBehavior
import tcssim.behavior.WfsSaveBehavior
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
      scs  <- ScsDB.build(srv, "m2:")
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
      oi   <- OiwfsDB.build(
                srv,
                "oiwfs:"
              )
      gmoi <- OiwfsDB.build(
                srv,
                "gmoi:"
              )
      f2oi <- OiwfsDB.build(
                srv,
                "f2oi:"
              )
      ac   <- AcDB.build(srv, "hrwfs:")
      ret  <- List(
                tcs.process,
                tcs.commands.apply.DIR.valueStream
                  .map(_.evalMap(_.map(carActivity(tcs, p1, p2, oi, gmoi, f2oi)).getOrElse(IO.unit)))
                  .map(List(_)),
                fullInpositionActivity(tcs, mcs, crcs).map(List(_)),
                crcs.process,
                mcs.process,
                scs.process,
                p1.process,
                p2.process,
                oi.process,
                gmoi.process,
                f2oi.process,
                ac.process
              ).sequence
    } yield ret.flatten

    r.use(
      Stream.emits[IO, Stream[IO, Unit]](_).parJoinUnbounded.compile.drain
    ).as(ExitCode.Success)
  }

  val BusyTime: FiniteDuration = 1.seconds

  def carActivity(
    db:   TcsEpicsDB[IO],
    p1:   WfsDB[IO],
    p2:   WfsDB[IO],
    oi:   OiwfsDB[IO],
    gmoi: OiwfsDB[IO],
    f2oi: OiwfsDB[IO]
  )(dir: CadDirective): IO[Unit] =
    if (dir === CadDirective.START)
      for {
        newClid <- db.commands.apply.CLID.getOption.map(x => Math.max(1, x.getOrElse(0) + 1))
        _       <- db.commands.apply.CLID.put(newClid)
        _       <- db.commands.apply.VAL.put(newClid)
        _       <- db.commands.apply.MESS.put("")
        _       <- db.commands.car.CLID.put(newClid)
        _       <- db.commands.car.OMSS.put("")
        _       <- db.commands.car.VAL.put(CarState.BUSY)
        _       <- runBehaviors(db, p1, p2, oi, gmoi, f2oi)
        _       <- db.clean
        _       <- IO.sleep(BusyTime)
        _       <- db.commands.car.VAL.put(CarState.IDLE)
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

  private def runBehaviors(
    db:   TcsEpicsDB[IO],
    p1:   WfsDB[IO],
    p2:   WfsDB[IO],
    oi:   OiwfsDB[IO],
    gmoi: OiwfsDB[IO],
    f2oi: OiwfsDB[IO]
  ): IO[Unit] =
    (TargetBehavior.allTargets[IO](db) ++
      List(
        GuiderBehavior.build[IO](db),
        WfsSaveBehavior.build(p1.commands, p1.status),
        WfsSaveBehavior.build(p2.commands, p2.status),
        WfsSaveBehavior.build(oi.wfsCommands, oi.status),
        WfsSaveBehavior.build(gmoi.wfsCommands, gmoi.status),
        WfsSaveBehavior.build(f2oi.wfsCommands, f2oi.status)
      )).map(_.process).parSequence.void

}
