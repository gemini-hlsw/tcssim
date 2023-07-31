// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Dispatcher
import cats.effect.syntax.all._
import com.cosylab.epics.caj.cas.util.DefaultServerImpl
import gov.aps.jca.JCALibrary
import org.typelevel.log4cats.Logger

import scala.reflect.ClassTag

import MemoryPV.ToDBRType

trait EpicsServer[F[_]] {

  def createPV[T: ToDBRType](name: String, init: Array[T]): Resource[F, MemoryPV[F, T]]

  def createPV1[T: ToDBRType: ClassTag](name: String, init: T): Resource[F, MemoryPV1[F, T]]

}

object EpicsServer {

  val jcaLibrary: JCALibrary = JCALibrary.getInstance()

  private final class EpicsServerImpl[F[_]: Async: Logger](
    dispatcher: Dispatcher[F],
    server:     DefaultServerImpl
  ) extends EpicsServer[F] {
    given Dispatcher[F] = dispatcher
    override def createPV[T: ToDBRType](name: String, init: Array[T]): Resource[F, MemoryPV[F, T]] =
      // MemoryPV.build(server, name, init)
      for {
        pv <- MemoryPV.build(server, name, init)
        st <- pv.valueStream.map(_.evalTap { x =>
                val s = x.toList.map(_.toString).mkString("[", ", ", "]")
                Logger[F].info(s"New value for channel $name = $s")
              })
        _  <- Resource.make(st.compile.drain.start)(_.cancel)
      } yield pv

    override def createPV1[T: ToDBRType: ClassTag](
      name: String,
      init: T
    ): Resource[F, MemoryPV1[F, T]] =
      // MemoryPV1.build(server, name, init)
      for {
        pv <- MemoryPV1.build(server, name, init)
        st <-
          pv.valueStream.map(_.evalTap(x => Logger[F].info(s"New value for channel $name = $x")))
        _  <- Resource.make(st.compile.drain.start)(_.cancel)
      } yield pv

  }

  def start[F[_]: Async: Logger](dispatcher: Dispatcher[F]): Resource[F, EpicsServer[F]] =
    for {
      server <- Resource.eval(Async[F].delay(new DefaultServerImpl()))
      ctx    <- Resource.make {
                  Async[F].delay(
                    jcaLibrary.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server)
                  )
                } { x =>
                  Async[F].delay(
                    x.dispose()
                  )
                }
      _      <- Resource.make {
                  Async[F].delay(ctx.run(0)).start
                }(_ =>
                  Async[F].delay {
                    ctx.shutdown()
                  }
                )
    } yield new EpicsServerImpl[F](dispatcher, server)

}
