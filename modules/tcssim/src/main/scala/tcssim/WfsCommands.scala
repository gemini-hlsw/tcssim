// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class WfsCommands[F[_]](
  gains:          CadRecord4[F],
  resetGains:     MemoryPV1[F, Double],
  circularBuffer: CadRecord4[F]
) {
  val cads: List[CadRecord[F]] = List(gains, circularBuffer)
}

object WfsCommands {

  val GainsCadName: String       = "dc:detSigInitFgGain"
  val CircularBufferName: String = "dc:detSigSaveCb"

  def build[F[_]: Monad](
    server:        EpicsServer[F],
    top:           String,
    gainResetName: String
  ): Resource[F, WfsCommands[F]] = for {
    gains <- CadRecord4.build(server, top + GainsCadName)
    rst   <- server.createPV1(top + gainResetName, 0.0)
    cbuf  <- CadRecord4.build(server, top + CircularBufferName)
  } yield WfsCommands(gains, rst, cbuf)

}
