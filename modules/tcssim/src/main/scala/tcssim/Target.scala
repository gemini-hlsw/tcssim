// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait Target[F[_]] {
  val objectName: MemoryPV1[F, String]
  val ra: MemoryPV1[F, Double]
  val dec: MemoryPV1[F, Double]
  val inputFrame: MemoryPV1[F, String]
  val equinox: MemoryPV1[F, String]
  val trackFrame: MemoryPV1[F, String]
  val wavelength: MemoryPV1[F, Double]
  val trackEquinox: MemoryPV1[F, String]
  val pmRA: MemoryPV1[F, Double]
  val pmDec: MemoryPV1[F, Double]
  val parallax: MemoryPV1[F, Double]
  val radialVelocity: MemoryPV1[F, Double]
  val epoch: MemoryPV1[F, String]
}

object Target {
  val ObjectNameSuffix: String     = "ObjectName.VAL"
  val RaSuffix: String             = "RA.VAL"
  val DecSuffix: String            = "Dec.VAL"
  val InputFrameSuffix: String     = "InputFrame.VAL"
  val EquinoxSuffix: String        = "Equinox.VAL"
  val TrackFrameSuffix: String     = "TrackFrame.VAL"
  val WavelengthSuffix: String     = "Wavelength.VAL"
  val TrackEquinoxSuffix: String   = "TrackEq.VAL"
  val PmRASuffix: String           = "PMRA.VAL"
  val PmDecSuffix: String          = "PMDec.VAL"
  val ParallaxSuffix: String       = "Parallax.VAL"
  val RadialVelocitySuffix: String = "RV.VAL"
  val EpochSuffix: String          = "Epoch.VAL"

  final case class TargetImpl[F[_]] private (
    objectName:     MemoryPV1[F, String],
    ra:             MemoryPV1[F, Double],
    dec:            MemoryPV1[F, Double],
    inputFrame:     MemoryPV1[F, String],
    equinox:        MemoryPV1[F, String],
    trackFrame:     MemoryPV1[F, String],
    wavelength:     MemoryPV1[F, Double],
    trackEquinox:   MemoryPV1[F, String],
    pmRA:           MemoryPV1[F, Double],
    pmDec:          MemoryPV1[F, Double],
    parallax:       MemoryPV1[F, Double],
    radialVelocity: MemoryPV1[F, Double],
    epoch:          MemoryPV1[F, String]
  ) extends Target[F]

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, Target[F]] = for {
    obn      <- server.createPV1[String](name + ObjectNameSuffix, "")
    ra       <- server.createPV1[Double](name + RaSuffix, 0.0)
    dec      <- server.createPV1[Double](name + DecSuffix, 0.0)
    ifr      <- server.createPV1[String](name + InputFrameSuffix, "")
    eq       <- server.createPV1[String](name + EquinoxSuffix, "")
    tfr      <- server.createPV1[String](name + TrackFrameSuffix, "")
    wl       <- server.createPV1[Double](name + WavelengthSuffix, 0.0)
    teq      <- server.createPV1[String](name + TrackEquinoxSuffix, "")
    pmra     <- server.createPV1(name + PmRASuffix, 0.0)
    pmdec    <- server.createPV1(name + PmDecSuffix, 0.0)
    parallax <- server.createPV1(name + ParallaxSuffix, 0.0)
    rv       <- server.createPV1(name + RadialVelocitySuffix, 0.0)
    epoch    <- server.createPV1[String](name + EpochSuffix, "")
  } yield TargetImpl(obn, ra, dec, ifr, eq, tfr, wl, teq, pmra, pmdec, parallax, rv, epoch)
}
