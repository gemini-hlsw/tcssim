// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

trait NodChopGuide[F[_]] {
  val nodachopa: MemoryPV1[F, String]
  val nodachopb: MemoryPV1[F, String]
  val nodachopc: MemoryPV1[F, String]
  val nodbchopa: MemoryPV1[F, String]
  val nodbchopb: MemoryPV1[F, String]
  val nodbchopc: MemoryPV1[F, String]
  val nodcchopa: MemoryPV1[F, String]
  val nodcchopb: MemoryPV1[F, String]
  val nodcchopc: MemoryPV1[F, String]
  val nodachopaDrv: MemoryPV1[F, Int]
  val nodachopbDrv: MemoryPV1[F, Int]
  val nodachopcDrv: MemoryPV1[F, Int]
  val nodbchopaDrv: MemoryPV1[F, Int]
  val nodbchopbDrv: MemoryPV1[F, Int]
  val nodbchopcDrv: MemoryPV1[F, Int]
  val nodcchopaDrv: MemoryPV1[F, Int]
  val nodcchopbDrv: MemoryPV1[F, Int]
  val nodcchopcDrv: MemoryPV1[F, Int]
}

object NodChopGuide {
  val ConfigPrefix: String       = "config"
  val NodachopaSuffix: String    = ".VALA"
  val NodachopbSuffix: String    = ".VALB"
  val NodachopcSuffix: String    = ".VALC"
  val NodbchopaSuffix: String    = ".VALD"
  val NodbchopbSuffix: String    = ".VALE"
  val NodbchopcSuffix: String    = ".VALF"
  val NodcchopaSuffix: String    = ".VALG"
  val NodcchopbSuffix: String    = ".VALH"
  val NodcchopcSuffix: String    = ".VALI"
  val DrivesSuffix: String       = "drives:"
  val NodachopaDrvSuffix: String = "nodAchopA.VAL"
  val NodachopbDrvSuffix: String = "nodAchopB.VAL"
  val NodachopcDrvSuffix: String = "nodAchopC.VAL"
  val NodbchopaDrvSuffix: String = "nodBchopA.VAL"
  val NodbchopbDrvSuffix: String = "nodBchopB.VAL"
  val NodbchopcDrvSuffix: String = "nodBchopC.VAL"
  val NodcchopaDrvSuffix: String = "nodCchopA.VAL"
  val NodcchopbDrvSuffix: String = "nodCchopB.VAL"
  val NodcchopcDrvSuffix: String = "nodCchopC.VAL"

  final case class NodChopGuideImpl[F[_]] private (
    nodachopa:    MemoryPV1[F, String],
    nodachopb:    MemoryPV1[F, String],
    nodachopc:    MemoryPV1[F, String],
    nodbchopa:    MemoryPV1[F, String],
    nodbchopb:    MemoryPV1[F, String],
    nodbchopc:    MemoryPV1[F, String],
    nodcchopa:    MemoryPV1[F, String],
    nodcchopb:    MemoryPV1[F, String],
    nodcchopc:    MemoryPV1[F, String],
    nodachopaDrv: MemoryPV1[F, Int],
    nodachopbDrv: MemoryPV1[F, Int],
    nodachopcDrv: MemoryPV1[F, Int],
    nodbchopaDrv: MemoryPV1[F, Int],
    nodbchopbDrv: MemoryPV1[F, Int],
    nodbchopcDrv: MemoryPV1[F, Int],
    nodcchopaDrv: MemoryPV1[F, Int],
    nodcchopbDrv: MemoryPV1[F, Int],
    nodcchopcDrv: MemoryPV1[F, Int]
  ) extends NodChopGuide[F]

  def build[F[_]](
    server: EpicsServer[F],
    top:    String,
    guider: String
  ): Resource[F, NodChopGuide[F]] = for {
    aa  <- server.createPV1(top + ConfigPrefix + guider + NodachopaSuffix, "Off")
    ab  <- server.createPV1(top + ConfigPrefix + guider + NodachopbSuffix, "Off")
    ac  <- server.createPV1(top + ConfigPrefix + guider + NodachopcSuffix, "Off")
    ba  <- server.createPV1(top + ConfigPrefix + guider + NodbchopaSuffix, "Off")
    bb  <- server.createPV1(top + ConfigPrefix + guider + NodbchopbSuffix, "Off")
    bc  <- server.createPV1(top + ConfigPrefix + guider + NodbchopcSuffix, "Off")
    ca  <- server.createPV1(top + ConfigPrefix + guider + NodcchopaSuffix, "Off")
    cb  <- server.createPV1(top + ConfigPrefix + guider + NodcchopbSuffix, "Off")
    cc  <- server.createPV1(top + ConfigPrefix + guider + NodcchopcSuffix, "Off")
    aad <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodachopaDrvSuffix, 0)
    abd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodachopbDrvSuffix, 0)
    acd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodachopcDrvSuffix, 0)
    bad <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodbchopaDrvSuffix, 0)
    bbd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodbchopbDrvSuffix, 0)
    bcd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodbchopcDrvSuffix, 0)
    cad <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodcchopaDrvSuffix, 0)
    cbd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodcchopbDrvSuffix, 0)
    ccd <- server.createPV1(top + DrivesSuffix + guider.toLowerCase + NodcchopcDrvSuffix, 0)
  } yield NodChopGuideImpl(aa,
                           ab,
                           ac,
                           ba,
                           bb,
                           bc,
                           ca,
                           cb,
                           cc,
                           aad,
                           abd,
                           acd,
                           bad,
                           bbd,
                           bcd,
                           cad,
                           cbd,
                           ccd
  )
}
