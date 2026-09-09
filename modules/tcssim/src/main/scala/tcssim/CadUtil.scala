// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Applicative
import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import fs2.Stream
import mouse.boolean.*
import tcssim.epics.EpicsServer
import tcssim.epics.MemoryPV1
import tcssim.epics.given

object CadUtil {

  val DirSuffix: String     = ".DIR"
  val MarkSuffix: String    = ".MARK"
  val InputASuffix: String  = ".A"
  val InputBSuffix: String  = ".B"
  val InputCSuffix: String  = ".C"
  val InputDSuffix: String  = ".D"
  val InputESuffix: String  = ".E"
  val InputFSuffix: String  = ".F"
  val InputGSuffix: String  = ".G"
  val InputHSuffix: String  = ".H"
  val InputISuffix: String  = ".I"
  val InputJSuffix: String  = ".J"
  val InputKSuffix: String  = ".K"
  val InputLSuffix: String  = ".L"
  val InputMSuffix: String  = ".M"
  val InputNSuffix: String  = ".N"
  val InputOSuffix: String  = ".O"
  val InputPSuffix: String  = ".P"
  val OutputASuffix: String = ".VALA"
  val OutputBSuffix: String = ".VALB"
  val OutputCSuffix: String = ".VALC"
  val OutputDSuffix: String = ".VALD"
  val OutputESuffix: String = ".VALE"
  val OutputFSuffix: String = ".VALF"
  val OutputGSuffix: String = ".VALG"
  val OutputHSuffix: String = ".VALH"
  val OutputISuffix: String = ".VALI"
  val OutputJSuffix: String = ".VALJ"
  val OutputKSuffix: String = ".VALK"
  val OutputLSuffix: String = ".VALL"
  val OutputMSuffix: String = ".VALM"
  val OutputNSuffix: String = ".VALN"
  val OutputOSuffix: String = ".VALO"
  val OutputPSuffix: String = ".VALP"

  def buildDir[F[_]](
    server: EpicsServer[F],
    name:   String
  ): Resource[F, MemoryPV1[F, CadDirective]] =
    server.createPV1(name + DirSuffix, CadDirective.CLEAR)

  def process[F[_]: Monad](
    dir:    MemoryPV1[F, CadDirective],
    mark:   MemoryPV1[F, Int],
    inputs: List[MemoryPV1[F, String]]
  ): Resource[F, List[Stream[F, Unit]]] = (
    inputs.map(
      _.valueStream.map(
        _.evalMap(
          _.as(mark.getOption.flatMap(_.forall(_ === 0).fold(mark.put(1), Applicative[F].unit)))
            .getOrElse(Applicative[F].unit)
        )
      )
    ) :+
      dir.valueStream.map {
        _.evalMap {
          case Some(CadDirective.MARK)  => mark.put(1)
          case Some(CadDirective.CLEAR) => mark.put(0)
          case _                        => Applicative[F].unit
        }
      }
  ).sequence

}
