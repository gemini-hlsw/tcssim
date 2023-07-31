// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics._
import tcssim.epics.given

trait CarRecord[F[_]] {
  val VAL: MemoryPV1[F, CarState]
  val CLID: MemoryPV1[F, Int]
  val OMSS: MemoryPV1[F, String]
}

object CarRecord {
  private case class CarRecordImpl[F[_]](
    VAL:  MemoryPV1[F, CarState],
    CLID: MemoryPV1[F, Int],
    OMSS: MemoryPV1[F, String]
  ) extends CarRecord[F]

  val ValSuffix: String  = ".VAL"
  val ClidSuffix: String = ".CLID"
  val OmssSuffix: String = ".OMSS"

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, CarRecord[F]] = for {
    v <- server.createPV1[CarState](name + ValSuffix, CarState.IDLE)
    c <- server.createPV1[Int](name + ClidSuffix, 0)
    o <- server.createPV1[String](name + OmssSuffix, "")
  } yield CarRecordImpl(v, c, o)

}
