// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.Monad
import cats.effect.Resource
import fs2.Stream
import tcssim.epics.EpicsServer

trait BaseSystemDB[F[_]] {
  val status: BaseSystem[F]

  def process: Resource[F, List[Stream[F, Unit]]]

  def clean: F[Unit]

}

object BaseSystemDB {
  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, BaseSystemDB[F]] = for {
    st <- BaseSystem.build(server, top)
  } yield new BaseSystemDB[F] {
    override val status: BaseSystem[F] = st

    override def process: Resource[F, List[Stream[F, Unit]]] = Resource.pure(List.empty)

    override def clean: F[Unit] = Applicative[F].unit
  }
}
