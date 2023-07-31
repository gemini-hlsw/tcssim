// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.Functor
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Dispatcher
import cats.syntax.all._
import com.cosylab.epics.caj.cas.util.DefaultServerImpl
import fs2.Stream
import tcssim.epics.MemoryPV.ToDBRType

import scala.reflect.ClassTag

trait MemoryPV1[F[_], T] {
  val getOption: F[Option[T]]
  def put(v: T): F[Unit]
  def valueStream: Resource[F, Stream[F, Option[T]]]
}

object MemoryPV1 {

  def build[F[_]: Async: Dispatcher, T: ToDBRType: ClassTag](
    server: DefaultServerImpl,
    name:   String,
    init:   T
  ): Resource[F, MemoryPV1[F, T]] =
    MemoryPV.build(server, name, Array(init)).map(v => new MemoryPV1Impl[F, T](v))

  private class MemoryPV1Impl[F[_]: Functor, T: ClassTag](
    m: MemoryPV[F, T]
  ) extends MemoryPV1[F, T] {
    override val getOption: F[Option[T]] = m.get.map(_.headOption)

    override def put(v: T): F[Unit] = m.put(Array(v))

    override def valueStream: Resource[F, Stream[F, Option[T]]] =
      m.valueStream.map(_.map(_.headOption))
  }

}
