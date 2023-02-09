// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.kernel.Resource
import tcssim.epics.EpicsServer

trait AltairCmds[F[_]] {
  val configForAo: CadRecord1[F]
  val aoDeployAdc: CadRecord1[F]
  val aoMoveGim: CadRecord3[F]
  val aoMoveAdc: CadRecord4[F]
  val aoPrepareCm: CadRecord6[F]
  val aoFlatten: CadRecord1[F]
  val aoCentreWfs: CadRecord1[F]
  val aoCorrect: CadRecord3[F]
  val aoEntShutter: CadRecord1[F]
  val aoExitShutter: CadRecord1[F]
  val aoDmVolt: CadRecord[F]
  val aoDatum: CadRecord1[F]
  val aoPark: CadRecord1[F]
  val aoOiwfsSource: CadRecord2[F]
  val aoLgsTTFSource: CadRecord2[F]
  val aoFLens: CadRecord1[F]
  val aoStats: CadRecord4[F]
}

object AltairCmds {

  val ConfigForAoSuffix: String    = "configForAo"
  val AoDeployAdcSuffix: String    = "aoDeployAdc"
  val AoMoveGimSuffix: String      = "aoMove"
  val AoMoveAdcSuffix: String      = "aoMoveAdc"
  val AoPrepareCmSuffix: String    = "aoPrepareCm"
  val AoFlattenSuffix: String      = "aoFlatten"
  val AoCentreWfsSuffix: String    = "aoCentreWfs"
  val AoCorrectSuffix: String      = "aoCorrect"
  val AoEntShutterSuffix: String   = "aoEntShutter"
  val AoExitShutterSuffix: String  = "aoExitShutter"
  val AoDmVoltSuffix: String       = "aoDmVolt"
  val AoDatumSuffix: String        = "aoDatum"
  val AoParkSuffix: String         = "aoPark"
  val AoOiwfsSourceSuffix: String  = "aoOiwfsSource"
  val AoLgsTTFSourceSuffix: String = "aoLgsTTFSource"
  val AoFLensSuffix: String        = "aoMoveAStar"
  val AoStatsSuffix: String        = "aoStats"

  final case class AltairCmdsImpl[F[_]] private (
    configForAo:    CadRecord1[F],
    aoDeployAdc:    CadRecord1[F],
    aoMoveGim:      CadRecord3[F],
    aoMoveAdc:      CadRecord4[F],
    aoPrepareCm:    CadRecord6[F],
    aoFlatten:      CadRecord1[F],
    aoCentreWfs:    CadRecord1[F],
    aoCorrect:      CadRecord3[F],
    aoEntShutter:   CadRecord1[F],
    aoExitShutter:  CadRecord1[F],
    aoDmVolt:       CadRecord[F],
    aoDatum:        CadRecord1[F],
    aoPark:         CadRecord1[F],
    aoOiwfsSource:  CadRecord2[F],
    aoLgsTTFSource: CadRecord2[F],
    aoFLens:        CadRecord1[F],
    aoStats:        CadRecord4[F]
  ) extends AltairCmds[F]

  def build[F[_]](server: EpicsServer[F], top: String): Resource[F, AltairCmds[F]] = for {
    configforao    <- CadRecord1.build(server, top + ConfigForAoSuffix)
    aodeployadc    <- CadRecord1.build(server, top + AoDeployAdcSuffix)
    aomovegim      <- CadRecord3.build(server, top + AoMoveGimSuffix)
    aomoveadc      <- CadRecord4.build(server, top + AoMoveAdcSuffix)
    aopreparecm    <- CadRecord6.build(server, top + AoPrepareCmSuffix)
    aoflatten      <- CadRecord1.build(server, top + AoFlattenSuffix)
    aocentrewfs    <- CadRecord1.build(server, top + AoCentreWfsSuffix)
    aocorrect      <- CadRecord3.build(server, top + AoCorrectSuffix)
    aoentshutter   <- CadRecord1.build(server, top + AoEntShutterSuffix)
    aoexitshutter  <- CadRecord1.build(server, top + AoExitShutterSuffix)
    aodmvolt       <- CadRecord.build(server, top + AoDmVoltSuffix)
    aodatum        <- CadRecord1.build(server, top + AoDatumSuffix)
    aopark         <- CadRecord1.build(server, top + AoParkSuffix)
    aooiwfssource  <- CadRecord2.build(server, top + AoOiwfsSourceSuffix)
    aolgsttfsource <- CadRecord2.build(server, top + AoLgsTTFSourceSuffix)
    aoflens        <- CadRecord1.build(server, top + AoFLensSuffix)
    aostats        <- CadRecord4.build(server, top + AoStatsSuffix)
  } yield AltairCmdsImpl(
    configforao,
    aodeployadc,
    aomovegim,
    aomoveadc,
    aopreparecm,
    aoflatten,
    aocentrewfs,
    aocorrect,
    aoentshutter,
    aoexitshutter,
    aodmvolt,
    aodatum,
    aopark,
    aooiwfssource,
    aolgsttfsource,
    aoflens,
    aostats
  )
}
