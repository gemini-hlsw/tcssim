// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.effect.{ Async, IO, Resource }
import cats.effect.std.Dispatcher
import com.cosylab.epics.caj.cas.util.DefaultServerImpl
import gov.aps.jca.JCALibrary
import MemoryPV.ToDBRType

import scala.reflect.ClassTag

trait EpicsServer[F[_]] {

  def createPV[T: ToDBRType](name: String, init: Array[T]): Resource[F, MemoryPV[F, T]]

  def createPV1[T: ToDBRType: ClassTag](name: String, init: T): Resource[F, MemoryPV1[F, T]]

}

object EpicsServer {

  val jcaLibrary: JCALibrary = JCALibrary.getInstance()

  private final class EpicsServerImpl[F[_]: Async: Dispatcher](server: DefaultServerImpl)
      extends EpicsServer[F] {

    override def createPV[T: ToDBRType](name: String, init: Array[T]): Resource[F, MemoryPV[F, T]] =
      MemoryPV.build(server, name, init)

    override def createPV1[T: ToDBRType: ClassTag](
      name: String,
      init: T
    ): Resource[F, MemoryPV1[F, T]] = MemoryPV1.build(server, name, init)

  }

  def start(implicit dispatcher: Dispatcher[IO]): Resource[IO, EpicsServer[IO]] =
    for {
      server <- Resource.eval(IO.delay(new DefaultServerImpl()))
      ctx    <- Resource.make {
                  IO.delay(
                    jcaLibrary.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server)
                  )
                } { x =>
                  IO.delay(
                    x.dispose()
                  )
                }
      _      <- Resource.make {
                  IO.delay(ctx.run(0)).start.void
                }(_ =>
                  IO.delay {
                    ctx.shutdown()
                  }
                )
    } yield new EpicsServerImpl[IO](server)

}
