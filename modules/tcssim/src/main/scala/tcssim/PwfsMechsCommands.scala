// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class PwfsMechsCommands[F[_]](
  filter:    MemoryPV1[F, String],
  fieldStop: MemoryPV1[F, String]
)

object PwfsMechsCommands {
  def build[F[_]](
    server: EpicsServer[F],
    top:    String,
    name:   String
  ): Resource[F, PwfsMechsCommands[F]] = for {
    flt <- server.createPV1(s"$top${name}Filter.A", "")
    fst <- server.createPV1(s"$top${name}Fldstop.A", "")
  } yield PwfsMechsCommands(flt, fst)

}
