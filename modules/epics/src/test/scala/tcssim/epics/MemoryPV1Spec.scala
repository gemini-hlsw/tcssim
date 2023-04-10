// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.effect.IO
import cats.effect.std.Dispatcher
import cats.syntax.all._
import munit.CatsEffectSuite
import MemoryPV.ToDBRType
import org.typelevel.log4cats.noop.NoOpLogger

import scala.reflect.ClassTag

class MemoryPV1Spec extends CatsEffectSuite {

  private implicit def logger = NoOpLogger.impl[IO]

  private val epicsServer = ResourceFixture {
    for {
      d <- Dispatcher[IO]
      s <- EpicsServer.start(d)
    } yield s
  }

  private def defineTests[T: ToDBRType: ClassTag](
    typeName: String,
    init:     T,
    writeVal: T,
    writeSeq: List[T]
  ): Unit = {

    epicsServer.test(s"Create $typeName channel") { srv =>
      assertIOBoolean(
        srv.createPV1[T]("dummy", init).use(_.getOption).as(true)
      )
    }

    epicsServer.test(s"Read $typeName channel") { srv =>
      assertIO(
        srv.createPV1("dummy", init).use(_.getOption),
        init.some
      )
    }

    epicsServer.test(s"Write $typeName channel") { srv =>
      assertIO(
        srv
          .createPV1[T]("dummy", init)
          .use { x =>
            x.put(writeVal) *>
              x.getOption
          },
        writeVal.some
      )
    }

    epicsServer.test(s"Monitor $typeName channel") { srv =>
      assertIOBoolean(
        (
          for {
            pv <- srv.createPV1[T]("dummy", init)
            s  <- pv.valueStream
          } yield (pv, s)
        ).use { case (x, s) =>
          writeSeq.map(x.put).sequence *>
            s.take(writeSeq.length.toLong).compile.toList.map(_.flattenOption)
        }.map(r => r.forall(writeSeq.contains) && writeSeq.forall(r.contains))
      )
    }
  }

  defineTests[Byte]("Byte", 0, 1, List.range(1: Byte, 5: Byte))

  defineTests[Short]("Short", 0, 1, List.range(1: Short, 5: Short))

  defineTests[Int]("Int", 0, 1, List.range(1, 5))

  defineTests[Float]("Float", 0.0f, 1.0f, List.range(1, 5).map(x => x.toFloat))

  defineTests[Double]("Double", 0.0, 1.0, List.range(1, 5).map(x => x.toDouble))

  defineTests[String]("String", "", "abcd", List.range(1, 5).map(x => x.toString))

  defineTests[TestEnum]("Enum",
                        TestEnum.VAL0,
                        TestEnum.VAL1,
                        List(TestEnum.VAL2, TestEnum.VAL1, TestEnum.VAL0)
  )

}
