// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.effect.Resource
import tcssim.epics.{ EpicsServer, MemoryPV1 }

object CadUtil {

  val DirSuffix: String    = ".DIR"
  val InputASuffix: String = ".A"
  val InputBSuffix: String = ".B"
  val InputCSuffix: String = ".C"
  val InputDSuffix: String = ".D"
  val InputESuffix: String = ".E"
  val InputFSuffix: String = ".F"
  val InputGSuffix: String = ".G"
  val InputHSuffix: String = ".H"
  val InputISuffix: String = ".I"
  val InputJSuffix: String = ".J"
  val InputKSuffix: String = ".K"
  val InputLSuffix: String = ".L"
  val InputMSuffix: String = ".M"
  val InputNSuffix: String = ".N"
  val InputOSuffix: String = ".O"
  val InputPSuffix: String = ".P"

  def buildDir[F[_]](
    server: EpicsServer[F],
    name:   String
  ): Resource[F, MemoryPV1[F, CadDirective]] =
    server.createPV1(name + DirSuffix, CadDirective.CLEAR)

}
