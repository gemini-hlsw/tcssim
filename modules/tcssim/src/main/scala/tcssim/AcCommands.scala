// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Monad
import cats.effect.Resource
import tcssim.epics.EpicsServer

case class AcCommands[F[_]](
  lensSel:      CadRecord1[F],
  ndFilterSel:  CadRecord1[F],
  clFilterSel:  CadRecord1[F],
  detExposure:  CadRecord2[F],
  setObserve:   CadRecord4[F],
  setDhsInfo:   CadRecord2[F],
  detObsType:   CadRecord1[F],
  detFrameSize: CadRecord6[F],
  observe:      CadRecord1[F],
  stop:         CadRecord[F]
) {
  val cads: List[CadRecord[F]] = List(lensSel,
                                      ndFilterSel,
                                      clFilterSel,
                                      detExposure,
                                      setObserve,
                                      setDhsInfo,
                                      detObsType,
                                      detFrameSize,
                                      observe,
                                      stop
  )
}

object AcCommands {

  def build[F[_]: Monad](
    server: EpicsServer[F],
    top:    String
  ): Resource[F, AcCommands[F]] = for {
    ls  <- CadRecord1.build[F](server, top + "lensSel")
    nd  <- CadRecord1.build[F](server, top + "ndfilterSel")
    cl  <- CadRecord1.build[F](server, top + "clfilterSel")
    de  <- CadRecord2.build(server, "dc:detExposure")
    so  <- CadRecord4.build(server, "dc:setObserve")
    dhs <- CadRecord2.build(server, "dc:setDhsInfo")
    ot  <- CadRecord1.build(server, "dc:detObstype")
    fs  <- CadRecord6.build(server, "dc:detFrameSize")
    ob  <- CadRecord1.build(server, "observe")
    st  <- CadRecord.build(server, "stop")
  } yield AcCommands[F](
    ls,
    nd,
    cl,
    de,
    so,
    dhs,
    ot,
    fs,
    ob,
    st
  )

}
