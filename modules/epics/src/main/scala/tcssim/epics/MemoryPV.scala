// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Dispatcher
import cats.effect.std.Queue
import cats.syntax.all._
import com.cosylab.epics.caj.cas.ProcessVariableEventDispatcher
import com.cosylab.epics.caj.cas.util.DefaultServerImpl
import com.cosylab.epics.caj.cas.util.MemoryProcessVariable
import fs2.Stream
import gov.aps.jca.Monitor
import gov.aps.jca.cas.ProcessVariableEventCallback
import gov.aps.jca.dbr.DBR
import gov.aps.jca.dbr.DBRType
import gov.aps.jca.dbr.TIME

trait MemoryPV[F[_], T] {
  val get: F[Array[T]]
  def put(v: Array[T]): F[Unit]
  def valueStream: Resource[F, Stream[F, Array[T]]]
}

object MemoryPV {

  trait ToDBRType[T] {
    type Dbr <: DBR
    val dbrType: DBRType
    def buildDBR(): Dbr with TIME
    def toEpicsVal(v:      Array[T]): Dbr
    def fromEpicsValue(v:  Dbr): Array[T]
    def extractOption(dbr: DBR): Option[Array[T]]
    def initValue(v:       Array[T]): Object = v
    val enumLabels: Array[String] = Array.empty
  }

  def build[F[_]: Async, T](server: DefaultServerImpl, name: String, init: Array[T])(implicit
    toDBRType:  ToDBRType[T],
    dispatcher: Dispatcher[F]
  ): Resource[F, MemoryPV[F, T]] = for {
    mpv <- Resource.make {
             Async[F].delay {
               val m = server.createMemoryProcessVariable(
                 name,
                 toDBRType.dbrType,
                 toDBRType.initValue(init)
               )
               m.setEnumLabels(toDBRType.enumLabels)
               m
             }
           } { x =>
             Async[F].delay {
               server.unregisterProcessVaribale(name)
               x.destroy()
             }
           }
    evd  = new ProcessVariableEventDispatcher(mpv)
    _   <- Resource.make(Async[F].delay(mpv.setEventCallback(evd)))(_ =>
             Async[F].delay(mpv.setEventCallback(null))
           )
  } yield new MemoryPVImpl[F, T](mpv, evd)

  private class MemoryPVImpl[F[_]: Async, T](
    mpv:             MemoryProcessVariable,
    eventDispatcher: ProcessVariableEventDispatcher
  )(implicit
    toDBRType:       ToDBRType[T],
    dispatcher:      Dispatcher[F]
  ) extends MemoryPV[F, T] {

    override val get: F[Array[T]] = Async[F].delay {
      val v: toDBRType.Dbr = toDBRType.buildDBR()
      mpv.read(v, null)
      toDBRType.fromEpicsValue(v)
    }

    override def put(v: Array[T]): F[Unit] = Async[F].delay {
      mpv.write(toDBRType.toEpicsVal(v), null)
    }.void

    override val valueStream: Resource[F, Stream[F, Array[T]]] = for {
      q <- Resource.eval(Queue.unbounded[F, Array[T]])
      c  = new ProcessVariableEventCallback {
             override def postEvent(select: Int, event: DBR): Unit =
               if ((select & Monitor.VALUE) =!= 0)
                 toDBRType
                   .extractOption(event)
                   .map(x => dispatcher.unsafeRunAndForget(q.offer(x)))
                   .getOrElse(())
               else ()

             override def canceled(): Unit = ()
           }
      _ <- Resource.make(Async[F].delay(eventDispatcher.registerEventListener(c)))(_ =>
             Async[F].delay(eventDispatcher.unregisterEventListener(c))
           )
    } yield Stream.fromQueueUnterminated(q)
  }

}
