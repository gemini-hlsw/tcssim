// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

sealed trait TrackLimits[F[_]] {
  val airMassLimit: MemoryPV1[F, Double]
  val airMassLimitEl: MemoryPV1[F, Double]
  val currentTimeToSet: MemoryPV1[F, String]
  val timeToAmSet: MemoryPV1[F, String]
  val timeToRotlim: MemoryPV1[F, String]
  val timeToAzlim: MemoryPV1[F, String]
  val timeToP1lim: MemoryPV1[F, String]
  val timeToP2lim: MemoryPV1[F, String]
  val timeToBlindSpot: MemoryPV1[F, String]
}

object TrackLimits {
  val AirMassLimitSuffix: String     = "airMassLimit.VAL"
  val AirMassLimitElSuffix: String   = "airMassLimitEl.VAL"
  val CurrentTimeToSetSuffix: String = "currentTimeToSet.VAL"
  val TimeToAmSetSuffix: String      = "timeToAmSet.VAL"
  val TimeToRotlimSuffix: String     = "timeToRotlim.VAL"
  val TimeToAzlimSuffix: String      = "timeToAzlim.VAL"
  val TimeToP1limSuffix: String      = "timeToP1lim.VAL"
  val TimeToP2limSuffix: String      = "timeToP2lim.VAL"
  val TimeToBlindSpotSuffix: String  = "timeToBlindSpot.VAL"

  case class TrackLimitsImpl[F[_]] private (
    airMassLimit:     MemoryPV1[F, Double],
    airMassLimitEl:   MemoryPV1[F, Double],
    currentTimeToSet: MemoryPV1[F, String],
    timeToAmSet:      MemoryPV1[F, String],
    timeToRotlim:     MemoryPV1[F, String],
    timeToAzlim:      MemoryPV1[F, String],
    timeToP1lim:      MemoryPV1[F, String],
    timeToP2lim:      MemoryPV1[F, String],
    timeToBlindSpot:  MemoryPV1[F, String]
  ) extends TrackLimits[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, TrackLimits[F]] = for {
    aml  <- server.createPV1[Double](prefix + AirMassLimitSuffix, 0.0)
    amel <- server.createPV1[Double](prefix + AirMassLimitElSuffix, 0.0)
    tts  <- server.createPV1[String](prefix + CurrentTimeToSetSuffix, "")
    tam  <- server.createPV1[String](prefix + TimeToAmSetSuffix, "")
    trt  <- server.createPV1[String](prefix + TimeToRotlimSuffix, "")
    taz  <- server.createPV1[String](prefix + TimeToAzlimSuffix, "")
    tp1  <- server.createPV1[String](prefix + TimeToP1limSuffix, "")
    tp2  <- server.createPV1[String](prefix + TimeToP2limSuffix, "")
    tbl  <- server.createPV1[String](prefix + TimeToBlindSpotSuffix, "")
  } yield TrackLimitsImpl(aml, amel, tts, tam, trt, taz, tp2, tp1, tbl)
}
