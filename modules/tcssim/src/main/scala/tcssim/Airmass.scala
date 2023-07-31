// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait Airmass[F[_]] {
  val airMass: MemoryPV1[F, Double]
  val amStart: MemoryPV1[F, Double]
  val amEnd: MemoryPV1[F, Double]
}

object Airmass {
  val AirMassSuffix: String = "airMass.VAL"
  val AmStartSuffix: String = "amStart.VAL"
  val AmEndSuffix: String   = "amEnd.VAL"

  private case class AirmassImpl[F[_]](
    airMass: MemoryPV1[F, Double],
    amStart: MemoryPV1[F, Double],
    amEnd:   MemoryPV1[F, Double]
  ) extends Airmass[F]

  def build[F[_]](server: EpicsServer[F], prefix: String): Resource[F, Airmass[F]] = for {
    airmass <- server.createPV1(prefix + AirMassSuffix, 0.0)
    amstart <- server.createPV1(prefix + AmStartSuffix, 0.0)
    amend   <- server.createPV1(prefix + AmEndSuffix, 0.0)
  } yield AirmassImpl(airmass, amstart, amend)
}
