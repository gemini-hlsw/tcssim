// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.effect.Resource
import fs2.Stream
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

import CadUtil._

trait CadRecord[F[_]] {
  val DIR: MemoryPV1[F, CadDirective]
  val MARK: MemoryPV1[F, Int]
  def clean: F[Unit]
  protected def inputs: List[MemoryPV1[F, String]] = List.empty
  def process: Resource[F, List[Stream[F, Unit]]]
}

object CadRecord {

  abstract class CadRecordImpl[F[_]: Applicative] extends CadRecord[F] {
    override def clean: F[Unit] = MARK.put(0)
    def process: Resource[F, List[Stream[F, Unit]]] = CadUtil.process(DIR, MARK, inputs)
  }

  def build[F[_]: Applicative](server: EpicsServer[F], cadName: String): Resource[F, CadRecord[F]] = for {
    dir <- buildDir(server, cadName)
    mark <- server.createPV1(cadName + MarkSuffix, 0)
  } yield new CadRecordImpl[F] {
    override val DIR: MemoryPV1[F, CadDirective] = dir
    override val MARK: MemoryPV1[F, Int] = mark
  }
}
