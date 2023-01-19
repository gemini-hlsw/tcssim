// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer

trait GemsCmds[F[_]] {
  val ngsPr1Ctrl: CadRecord1[F]
  val ngsPr2Ctrl: CadRecord1[F]
  val ngsPr3Ctrl: CadRecord1[F]
  val odgw1Park: CadRecord1[F]
  val odgw2Park: CadRecord1[F]
  val odgw3Park: CadRecord1[F]
  val odgw4Park: CadRecord1[F]
}

object GemsCmds {
  val NgsPr1CtrlSuffix: String = "ngsPr1Park"
  val NgsPr2CtrlSuffix: String = "ngsPr2Park"
  val NgsPr3CtrlSuffix: String = "ngsPr3Park"
  val Odgw1ParkSuffix: String  = "odgw1Park"
  val Odgw2ParkSuffix: String  = "odgw2Park"
  val Odgw3ParkSuffix: String  = "odgw3Park"
  val Odgw4ParkSuffix: String  = "odgw4Park"

  final case class GemsCmdsImpl[F[_]] private (
    ngsPr1Ctrl: CadRecord1[F],
    ngsPr2Ctrl: CadRecord1[F],
    ngsPr3Ctrl: CadRecord1[F],
    odgw1Park:  CadRecord1[F],
    odgw2Park:  CadRecord1[F],
    odgw3Park:  CadRecord1[F],
    odgw4Park:  CadRecord1[F]
  ) extends GemsCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, GemsCmds[F]] = for {
    ngspr1ctrl <- CadRecord1.build(server, top + NgsPr1CtrlSuffix)
    ngspr2ctrl <- CadRecord1.build(server, top + NgsPr2CtrlSuffix)
    ngspr3ctrl <- CadRecord1.build(server, top + NgsPr3CtrlSuffix)
    odgw1park  <- CadRecord1.build(server, top + Odgw1ParkSuffix)
    odgw2park  <- CadRecord1.build(server, top + Odgw2ParkSuffix)
    odgw3park  <- CadRecord1.build(server, top + Odgw3ParkSuffix)
    odgw4park  <- CadRecord1.build(server, top + Odgw4ParkSuffix)
  } yield GemsCmdsImpl(ngspr1ctrl,
                       ngspr2ctrl,
                       ngspr3ctrl,
                       odgw1park,
                       odgw2park,
                       odgw3park,
                       odgw4park
  )
}
