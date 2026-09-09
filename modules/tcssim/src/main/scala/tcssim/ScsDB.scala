// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.Monad
import cats.effect.Resource
import fs2.Stream
import tcssim.epics.EpicsServer

trait ScsDB[F[_]] {
  val status: ScsStatus[F]
  def process: Resource[F, List[Stream[F, Unit]]]
  def clean: F[Unit]
}

object ScsDB {
  def build[F[_]: Monad](server: EpicsServer[F], top: String): Resource[F, ScsDB[F]] = for {
    st <- ScsStatus.build(server, top)
  } yield new ScsDB[F] {
    override val status: ScsStatus[F] = st

    override def process: Resource[F, List[Stream[F, Unit]]] = Resource.pure(List.empty)

    override def clean: F[Unit] = Applicative[F].unit
  }

}
