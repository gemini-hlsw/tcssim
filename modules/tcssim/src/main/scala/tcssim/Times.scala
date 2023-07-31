// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait Times[F[_]] {
  val UTC: MemoryPV1[F, String]
  val LST: MemoryPV1[F, String]
  val ttmai: MemoryPV1[F, Double]
  val djmls: MemoryPV1[F, Double]
  val delat: MemoryPV1[F, Double]
  val delut: MemoryPV1[F, Double]
  val localTime: MemoryPV1[F, String]
  val date: MemoryPV1[F, String]
}

object Times {
  val UTCSuffix: String     = "UTC.VAL"
  val LSTSuffix: String     = "LST.VAL"
  val TtmaiSuffix: String   = "ttmai.VAL"
  val DjmlsSuffix: String   = "djmls.VAL"
  val DelatSuffix: String   = "delat.VAL"
  val DelutSuffix: String   = "delut.VAL"
  val LocalTimeName: String = "localTime.VAL"
  val DateName: String      = "date.VAL"

  case class TimesImpl[F[_]](
    UTC:       MemoryPV1[F, String],
    LST:       MemoryPV1[F, String],
    ttmai:     MemoryPV1[F, Double],
    djmls:     MemoryPV1[F, Double],
    delat:     MemoryPV1[F, Double],
    delut:     MemoryPV1[F, Double],
    localTime: MemoryPV1[F, String],
    date:      MemoryPV1[F, String]
  ) extends Times[F]

  def build[F[_]](server: EpicsServer[F], top: String, prefix: String): Resource[F, Times[F]] =
    for {
      utc   <- server.createPV1(top + UTCSuffix, "00:00:00")
      lst   <- server.createPV1(top + LSTSuffix, "00:00:00")
      ttmai <- server.createPV1(prefix + TtmaiSuffix, 0.0)
      djmls <- server.createPV1(prefix + DjmlsSuffix, 0.0)
      delat <- server.createPV1(prefix + DelatSuffix, 0.0)
      delut <- server.createPV1(prefix + DelutSuffix, 0.0)
      lt    <- server.createPV1(prefix + LocalTimeName, "00:00:00")
      date  <- server.createPV1(prefix + DateName, "00:00:00")
    } yield TimesImpl(utc, lst, ttmai, djmls, delat, delut, lt, date)
}
