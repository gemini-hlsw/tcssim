// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import tcssim.epics.EpicsServer

trait RotatorCmds[F[_]] {
  val park: CadRecord[F]
  val stop: CadRecord2[F]
  val move: CadRecord1[F]

  def cads: List[CadRecord[F]]
}

object RotatorCmds {

  private val rotatorParkSuffix: String = "rotPark"
  private val rotatorStopSuffix: String = "rotStop"
  private val rotatorMove: String       = "rotMove"

  private case class RotatorCmdsImpl[F[_]](
    override val park: CadRecord[F],
    override val stop: CadRecord2[F],
    override val move: CadRecord1[F]
  ) extends RotatorCmds[F] {
    override def cads: List[CadRecord[F]] =
      List(
        park,
        stop,
        move
      )

  }

  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, RotatorCmds[F]] =
    for {
      park <- CadRecord.build(server, top + rotatorParkSuffix)
      stop <- CadRecord2.build(server, top + rotatorStopSuffix)
      move <- CadRecord1.build(server, top + rotatorMove)
    } yield RotatorCmdsImpl(park, stop, move)

}
