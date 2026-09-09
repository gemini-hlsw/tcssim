// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

case class AcStatus[F[_]](
  health:   MemoryPV1[F, String],
  lens:     MemoryPV1[F, String],
  filter:   MemoryPV1[F, String],
  ndFilter: MemoryPV1[F, String],
  observeC: CarRecord[F]
)

object AcStatus {

  def build[F[_]](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, AcStatus[F]] = for {
    hlt <- server.createPV1(top + "health.VAL", "GOOD")
    lns <- server.createPV1(top + "lensName.VAL", "AC")
    flt <- server.createPV1(top + "clfilterName.VAL", "neutral")
    ndf <- server.createPV1(top + "ndfilterName.VAL", "open")
    oc  <- CarRecord.build[F](server, top + "observeC")
  } yield AcStatus[F](
    hlt,
    lns,
    flt,
    ndf,
    oc
  )

}
