// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.std.Dispatcher
import cats.effect.{ ExitCode, IO, IOApp, Resource }
import cats.implicits.catsSyntaxEq
import tcssim.epics.EpicsServer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

object TcsSimApp extends IOApp {

  private implicit def L: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("tcssim")

  override def run(args: List[String]): IO[ExitCode] = {
    val r = for {
      _   <- Resource.eval(printBanner)
      dsp <- Dispatcher[IO]
      srv <- EpicsServer.start(dsp)
      db  <- TcsEpicsDB.build(srv, "tc1:")
    } yield db

    r.use(process).as(ExitCode.Success)
  }

  def process(db: TcsEpicsDB[IO]): IO[Unit] =
    db.commands.apply.DIR.valueStream.use { ss =>
      ss.evalMap(_.map(carActivity(db)).getOrElse(IO.unit)).compile.drain
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
        _    <- IO.sleep(BusyTime)
        _    <- db.commands.car.VAL.put(CarState.IDLE)
      } yield ()
    else IO.unit

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

}
