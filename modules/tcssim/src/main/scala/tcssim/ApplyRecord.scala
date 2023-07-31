// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics._
import tcssim.epics.given

import CadDirective._

trait ApplyRecord[F[_]] extends Product with Serializable {
  val DIR: MemoryPV1[F, CadDirective]
  val VAL: MemoryPV1[F, Int]
  val CLID: MemoryPV1[F, Int]
  val MESS: MemoryPV1[F, String]
}

object ApplyRecord {
  val DirSuffix: String  = ".DIR"
  val ValSuffix: String  = ".VAL"
  val ClidSuffix: String = ".CLID"
  val MessSuffix: String = ".MESS"

  private case class ApplyRecordImpl[F[_]](
    DIR:  MemoryPV1[F, CadDirective],
    VAL:  MemoryPV1[F, Int],
    CLID: MemoryPV1[F, Int],
    MESS: MemoryPV1[F, String]
  ) extends ApplyRecord[F]

  def build[F[_]](server: EpicsServer[F], name: String): Resource[F, ApplyRecord[F]] = for {
    d <- server.createPV1[CadDirective](name + DirSuffix, CLEAR)
    v <- server.createPV1[Int](name + ValSuffix, 0)
    c <- server.createPV1[Int](name + ClidSuffix, 0)
    o <- server.createPV1[String](name + MessSuffix, "")
  } yield ApplyRecordImpl(d, v, c, o)

}
