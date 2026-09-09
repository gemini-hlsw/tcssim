// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.AG.PwfsMechsStatus
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

private case class AG[F[_]](
  health:        MemoryPV1[F, String],
  port1:         MemoryPV1[F, String],
  port2:         MemoryPV1[F, String],
  port3:         MemoryPV1[F, String],
  port4:         MemoryPV1[F, String],
  port5:         MemoryPV1[F, String],
  f2:            MemoryPV1[F, Int],
  gsaoi:         MemoryPV1[F, Int],
  nifs:          MemoryPV1[F, Int],
  nirs:          MemoryPV1[F, Int],
  niri:          MemoryPV1[F, Int],
  gmos:          MemoryPV1[F, Int],
  gpi:           MemoryPV1[F, Int],
  ghost:         MemoryPV1[F, Int],
  oiProbeParked: MemoryPV1[F, Int],
  oiFollowS:     MemoryPV1[F, String],
  oiName:        MemoryPV1[F, String],
  p1ProbeParked: MemoryPV1[F, Int],
  p1FollowS:     MemoryPV1[F, String],
  p2ProbeParked: MemoryPV1[F, Int],
  p2FollowS:     MemoryPV1[F, String],
  aoName:        MemoryPV1[F, String],
  hwName:        MemoryPV1[F, String],
  sfName:        MemoryPV1[F, String],
  sfParked:      MemoryPV1[F, Int],
  hwParked:      MemoryPV1[F, Int],
  sfRot:         MemoryPV1[F, Double],
  sfTilt:        MemoryPV1[F, Double],
  sfLin:         MemoryPV1[F, Double],
  pwfs1Mechs:    PwfsMechsStatus[F],
  pwfs2Mechs:    PwfsMechsStatus[F],
  aoParked:      MemoryPV1[F, Int]
)

object AG {
  val PortSuffix: String          = "port:"
  val OISuffix: String            = "oi:"
  val P1Suffix: String            = "p1:"
  val P2Suffix: String            = "p2:"
  val Port1Suffix: String         = "port1.VAL"
  val Port2Suffix: String         = "port2.VAL"
  val Port3Suffix: String         = "port3.VAL"
  val Port4Suffix: String         = "port4.VAL"
  val Port5Suffix: String         = "port5.VAL"
  val F2Suffix: String            = "f2.VAL"
  val GsaoiSuffix: String         = "gsaoi.VAL"
  val NirsSuffix: String          = "nirs.VAL"
  val NifsSuffix: String          = "nifs.VAL"
  val NiriSuffix: String          = "niri.VAL"
  val GmosSuffix: String          = "gmos.VAL"
  val GpiSuffix: String           = "gpi.VAL"
  val GhostSuffix: String         = "ghost.VAL"
  val AONameSuffix: String        = "aoName.VAL"
  val HWNameSuffix: String        = "hwName.VAL"
  val SFNameSuffix: String        = "sfName.VAL"
  val SFParkedSuffix: String      = "sfParked.VAL"
  val HWParkedSuffix: String      = "hwParked.VAL"
  val OINameSuffix: String        = "name.VAL"
  val OIProbeParkedSuffix: String = "probeParked.VAL"
  val OIFollowSSuffix: String     = "followS.VAL"
  val InPositionSuffix: String    = "inPosition.VAL"
  val SFRot: String               = "sfRot.VAL"
  val SFTilt: String              = "sfTilt.VAL"
  val SFLin: String               = "sfLin.VAL"
  val Health: String              = "health.VAL"
  val AoParked: String            = "aoParked.VAL"

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, AG[F]] = for {
    hlt           <- server.createPV1(top + Health, "GOOD")
    port1         <- server.createPV1(top + PortSuffix + Port1Suffix, "GMOS")
    port2         <- server.createPV1(top + PortSuffix + Port2Suffix, "F2")
    port3         <- server.createPV1(top + PortSuffix + Port3Suffix, "GHOST")
    port4         <- server.createPV1(top + PortSuffix + Port4Suffix, "GCAL")
    port5         <- server.createPV1(top + PortSuffix + Port5Suffix, "GSOAI")
    f2            <- server.createPV1(top + PortSuffix + F2Suffix, 0)
    gsaoi         <- server.createPV1(top + PortSuffix + GsaoiSuffix, 0)
    nifs          <- server.createPV1(top + PortSuffix + NifsSuffix, 0)
    nirs          <- server.createPV1(top + PortSuffix + NirsSuffix, 0)
    niri          <- server.createPV1(top + PortSuffix + NiriSuffix, 0)
    gmos          <- server.createPV1(top + PortSuffix + GmosSuffix, 0)
    gpi           <- server.createPV1(top + PortSuffix + GpiSuffix, 0)
    ghost         <- server.createPV1(top + PortSuffix + GhostSuffix, 0)
    aoName        <- server.createPV1(top + AONameSuffix, "")
    hwName        <- server.createPV1(top + HWNameSuffix, "")
    sfName        <- server.createPV1(top + SFNameSuffix, "")
    oiName        <- server.createPV1(top + OISuffix + OINameSuffix, "OIWFS")
    oiProbeParked <- server.createPV1(top + OISuffix + OIProbeParkedSuffix, 0)
    oiFollowS     <- server.createPV1(top + OISuffix + OIFollowSSuffix, "On")
    p1ProbeParked <- server.createPV1(top + P1Suffix + OIProbeParkedSuffix, 0)
    p1FollowS     <- server.createPV1(top + P1Suffix + OIFollowSSuffix, "ON")
    p2ProbeParked <- server.createPV1(top + P2Suffix + OIProbeParkedSuffix, 0)
    p2FollowS     <- server.createPV1(top + P2Suffix + OIFollowSSuffix, "ON")
    sfParked      <- server.createPV1(top + SFParkedSuffix, 0)
    hwParked      <- server.createPV1(top + HWParkedSuffix, 0)
    inPosition    <- server.createPV1(top + InPositionSuffix, 1)
    sfRot         <- server.createPV1(top + SFRot, 0.0)
    sfTilt        <- server.createPV1(top + SFTilt, 0.0)
    sfLin         <- server.createPV1(top + SFLin, 0.0)
    p1            <- PwfsMechsStatus.build(server, top, "p1")
    p2            <- PwfsMechsStatus.build(server, top, "p2")
    aop           <- server.createPV1(top + AoParked, 0)
  } yield AG(
    hlt,
    port1,
    port2,
    port3,
    port4,
    port5,
    f2,
    gsaoi,
    nifs,
    nirs,
    niri,
    gmos,
    gpi,
    ghost,
    oiProbeParked,
    oiFollowS,
    oiName,
    p1ProbeParked,
    p1FollowS,
    p2ProbeParked,
    p2FollowS,
    aoName,
    hwName,
    sfName,
    sfParked,
    hwParked,
    sfRot,
    sfTilt,
    sfLin,
    p1,
    p2,
    aop
  )

  case class PwfsMechsStatus[F[_]](
    filter:    MemoryPV1[F, String],
    fieldStop: MemoryPV1[F, String]
  )

  object PwfsMechsStatus {
    def build[F[_]](
      server: EpicsServer[F],
      top:    String,
      name:   String
    ): Resource[F, PwfsMechsStatus[F]] = for {
      flt <- server.createPV1(s"$top$name:filterName.VAL", "neutral")
      fst <- server.createPV1(s"$top$name:fldstopName.VAL", "open1")
    } yield PwfsMechsStatus(flt, fst)
  }

}
