// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

case class OiwfsCommands[F[_]] private (
  dark:    CadRecord4[F],
  sigMode: CadRecord16[F],
  sigInit: CadRecord2[F]
) {
  val cads: List[CadRecord[F]] = List(dark, sigMode, sigInit)
}

object OiwfsCommands {
  def build[F[_]: Monad](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, OiwfsCommands[F]] = for {
    dk <- CadRecord4.build(server, s"${top}dc:detSigModeSeqDark")
    sm <- CadRecord16.build(server, s"${top}dc:detSigModeSeq")
    si <- CadRecord2.build(server, s"${top}dc:detSigInit")
  } yield OiwfsCommands(dk, sm, si)

}
