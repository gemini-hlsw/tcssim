// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }
import Beam._

trait NodChopStat[F[_]] {
  val nodState: MemoryPV1[F, String]
  val chopState: MemoryPV1[F, String]
  val chopBeam: MemoryPV1[F, Beam]
  val chopBeamS: MemoryPV1[F, String]
  val chopFreq: MemoryPV1[F, Double]
  val chopPA: MemoryPV1[F, Double]
  val chopThrow: MemoryPV1[F, Double]
  val chopDutyCycle: MemoryPV1[F, Double]
}

object NodChopStat {
  val NodStateName: String      = "drives:nodState.VAL"
  val ChopStateName: String     = "chopState.VAL"
  val ChopBeamName: String      = "nod:chopBeam.VAL"
  val ChopBeamSName: String     = "chopBeam.VAL"
  val ChopFreqName: String      = "chopFreq.VAL"
  val ChopPAName: String        = "chopPA.VAL"
  val ChopThrowName: String     = "chopThrow.VAL"
  val ChopDutyCycleName: String = "chopDutyCycle.VAL"

  final case class NodChopStatImpl[F[_]] private (
    nodState:      MemoryPV1[F, String],
    chopState:     MemoryPV1[F, String],
    chopBeam:      MemoryPV1[F, Beam],
    chopBeamS:     MemoryPV1[F, String],
    chopFreq:      MemoryPV1[F, Double],
    chopPA:        MemoryPV1[F, Double],
    chopThrow:     MemoryPV1[F, Double],
    chopDutyCycle: MemoryPV1[F, Double]
  ) extends NodChopStat[F]

  def build[F[_]](
    server: EpicsServer[F],
    top:    String,
    prefix: String
  ): Resource[F, NodChopStat[F]] = for {
    nodstate      <- server.createPV1(top + NodStateName, "A")
    chopstate     <- server.createPV1(prefix + ChopStateName, "A")
    chopbeam      <- server.createPV1[Beam](top + ChopBeamName, A)
    chopbeamS     <- server.createPV1(prefix + ChopBeamSName, "A")
    chopfreq      <- server.createPV1(prefix + ChopFreqName, 0.0)
    choppa        <- server.createPV1(prefix + ChopPAName, 0.0)
    chopthrow     <- server.createPV1(prefix + ChopThrowName, 0.0)
    chopdutycycle <- server.createPV1(prefix + ChopDutyCycleName, 0.0)

  } yield NodChopStatImpl(nodstate,
                          chopstate,
                          chopbeam,
                          chopbeamS,
                          chopfreq,
                          choppa,
                          chopthrow,
                          chopdutycycle
  )

}
