// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer

trait WfsCommands[F[_]] {
  val p1Observe: CadRecord7[F]
  val p1Stop: CadRecord[F]
  val p2Observe: CadRecord7[F]
  val p2Stop: CadRecord[F]
  val oiObserve: CadRecord7[F]
  val oiStop: CadRecord[F]
}

object WfsCommands {
  val ObserveCadName: String = "Observe"
  val StopCadName: String    = "StopObserve"
  val P1Prefix: String       = "pwfs1"
  val P2Prefix: String       = "pwfs2"
  val OiPrefix: String       = "oiwfs"

  final case class WfsCommandsImpl[F[_]] private (
    p1Observe: CadRecord7[F],
    p1Stop:    CadRecord[F],
    p2Observe: CadRecord7[F],
    p2Stop:    CadRecord[F],
    oiObserve: CadRecord7[F],
    oiStop:    CadRecord[F]
  ) extends WfsCommands[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, WfsCommands[F]] = for {
    p1o <- CadRecord7.build(server, top + P1Prefix + ObserveCadName)
    p1s <- CadRecord.build(server, top + P1Prefix + StopCadName)
    p2o <- CadRecord7.build(server, top + P2Prefix + ObserveCadName)
    p2s <- CadRecord.build(server, top + P2Prefix + StopCadName)
    oio <- CadRecord7.build(server, top + OiPrefix + ObserveCadName)
    ois <- CadRecord.build(server, top + OiPrefix + StopCadName)
  } yield WfsCommandsImpl(p1o, p1s, p2o, p2s, oio, ois)
}
