// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer

trait ConfigCmds[F[_]] {
  val slew: CadRecord16[F]
  val rotator: CadRecord4[F]
  val filter1: CadRecord4[F]
  val chopRelative: CadRecord4[F]
  val chopConfig: CadRecord4[F]
}

object ConfigCmds {
  val SlewName: String         = "slew"
  val RotatorName: String      = "rotator"
  val Filter1Name: String      = "filter1"
  val ChopRelativeName: String = "chopRelative"
  val ChopConfigName: String   = "chopConfig"

  final case class ConfigCmdsImpl[F[_]] private (
    slew:         CadRecord16[F],
    rotator:      CadRecord4[F],
    filter1:      CadRecord4[F],
    chopRelative: CadRecord4[F],
    chopConfig:   CadRecord4[F]
  ) extends ConfigCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, ConfigCmds[F]] = for {
    slew         <- CadRecord16.build(server, top + SlewName)
    rotator      <- CadRecord4.build(server, top + RotatorName)
    filter1      <- CadRecord4.build(server, top + Filter1Name)
    choprelative <- CadRecord4.build(server, top + ChopRelativeName)
    chopcfg      <- CadRecord4.build(server, top + ChopConfigName)
  } yield ConfigCmdsImpl(slew, rotator, filter1, choprelative, chopcfg)
}
