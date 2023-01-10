// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.effect.IO
import cats.effect.std.Dispatcher
import cats.syntax.all._
import munit.CatsEffectSuite
import MemoryPV.ToDBRType

class MemoryPVSpec extends CatsEffectSuite {

  private val epicsServer = ResourceFixture {
    for {
      d <- Dispatcher[IO]
      s <- EpicsServer.start(d)
    } yield s
  }

  private def defineTests[T: ToDBRType](
    typeName: String,
    init:     Array[T],
    writeVal: Array[T],
    writeSeq: List[Array[T]]
  ): Unit = {

    epicsServer.test(s"Create $typeName channel") { srv =>
      assertIOBoolean(
        srv.createPV[T]("dummy", init).use(_.get).map(_.toSeq).as(true)
      )
    }

    epicsServer.test(s"Read $typeName channel") { srv =>
      assertIO(
        srv.createPV("dummy", init).use(_.get).map(_.toSeq),
        init.toSeq
      )
    }

    epicsServer.test(s"Write $typeName channel") { srv =>
      assertIO(
        srv
          .createPV[T]("dummy", init)
          .use { x =>
            x.put(writeVal) *>
              x.get
          }
          .map(_.toSeq),
        writeVal.toSeq
      )
    }

    epicsServer.test(s"Monitor $typeName channel") { srv =>
      val checkVal = writeSeq.map(_.toSeq)
      assertIOBoolean(
        (for {
          pv <- srv.createPV[T]("dummy", init)
          s  <- pv.valueStream
        } yield (pv, s))
          .use { case (x, s) =>
            writeSeq.map(x.put).sequence *>
              s.take(writeSeq.length.toLong).compile.toList.map(_.map(_.toSeq))
          }
          .map(r => r.forall(checkVal.contains) && checkVal.forall(r.contains))
      )
    }
  }

  defineTests[Byte]("Byte", Array(0), Array(1), List.range(1: Byte, 5: Byte).map(Array(_)))

  defineTests[Short]("Short", Array(0), Array(1), List.range(1: Short, 5: Short).map(Array(_)))

  defineTests[Int]("Int", Array(0), Array(1), List.range(1, 5).map(Array(_)))

  defineTests[Float]("Float", Array(0.0f), Array(1.0f), List.range(1, 5).map(x => Array(x.toFloat)))

  defineTests[Double]("Double",
                      Array(0.0),
                      Array(1.0),
                      List.range(1, 5).map(x => Array(x.toDouble))
  )

  defineTests[String]("String",
                      Array(""),
                      Array("abcd"),
                      List.range(1, 5).map(x => Array(x.toString))
  )

  defineTests[TestEnum]("Enum",
                        Array(TestEnum.VAL0),
                        Array(TestEnum.VAL1),
                        List(TestEnum.VAL2, TestEnum.VAL1, TestEnum.VAL0).map(Array(_))
  )

}
