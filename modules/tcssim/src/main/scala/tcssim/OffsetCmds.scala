// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.syntax.all.*
import cats.effect.Resource
import tcssim.epics.EpicsServer

trait OffsetCmds[F[_]] {
  val offsetPoA: CadRecord2[F]
  val offsetPoA1: CadRecord2[F]
  val offsetPoB: CadRecord2[F]
  val offsetPoB1: CadRecord2[F]
  val offsetPoC: CadRecord2[F]
  val offsetPoC1: CadRecord2[F]

  def cads: List[CadRecord[F]]
}

object OffsetCmds {
  val OffsetPoASuffix: String  = "offsetPoA"
  val OffsetPoA1Suffix: String = "offsetPoA1"
  val OffsetPoBSuffix: String  = "offsetPoB"
  val OffsetPoB1Suffix: String = "offsetPoB1"
  val OffsetPoCSuffix: String  = "offsetPoC"
  val OffsetPoC1Suffix: String = "offsetPoC1"

  private case class OffsetCmdsImpl[F[_]](
    offsetPoA:  CadRecord2[F],
    offsetPoA1: CadRecord2[F],
    offsetPoB:  CadRecord2[F],
    offsetPoB1: CadRecord2[F],
    offsetPoC:  CadRecord2[F],
    offsetPoC1: CadRecord2[F]
  ) extends OffsetCmds[F] {
    override def cads: List[CadRecord[F]] =
      List(
        offsetPoA,
        offsetPoA1,
        offsetPoB,
        offsetPoB1,
        offsetPoC,
        offsetPoC1
      )

  }

  def build[F[_]: Applicative](server: EpicsServer[F], top: String): Resource[F, OffsetCmds[F]] = for {
    poa  <- CadRecord2.build(server, top + OffsetPoASuffix)
    poa1 <- CadRecord2.build(server, top + OffsetPoA1Suffix)
    pob  <- CadRecord2.build(server, top + OffsetPoBSuffix)
    pob1 <- CadRecord2.build(server, top + OffsetPoB1Suffix)
    poc  <- CadRecord2.build(server, top + OffsetPoCSuffix)
    poc1 <- CadRecord2.build(server, top + OffsetPoC1Suffix)
  } yield OffsetCmdsImpl(poa, poa1, pob, pob1, poc, poc1)
}
