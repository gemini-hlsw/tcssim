// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

trait ScsStatus[F[_]] {
  val health: MemoryPV1[F, String]
  val followS: MemoryPV1[F, String]
  val centralBaffle: MemoryPV1[F, String]
  val deployableBaffle: MemoryPV1[F, String]
}

object ScsStatus {
  def build[F[_]](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, ScsStatus[F]] = for {
    base <- BaseSystem.build(server, top)
    ceBa <- server.createPV1[String](s"${top}CentralBafflePos.VAL", "Open")
    dpBa <- server.createPV1[String](s"${top}DeployBafflePos.VAL", "Visible")
  } yield new ScsStatus[F] {
    export base.*
    override val centralBaffle: MemoryPV1[F, String]    = ceBa
    override val deployableBaffle: MemoryPV1[F, String] = dpBa
  }
}
