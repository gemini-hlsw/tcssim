// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Eq
import lucuma.core.util.Enumerated

trait BinaryOnOff extends Product with Serializable

object BinaryOnOff {
  case object Off extends BinaryOnOff
  case object On  extends BinaryOnOff

  implicit val binaryOnOffEq: Eq[BinaryOnOff] = Eq.fromUniversalEquals

  implicit val binaryOnOffEnum: Enumerated[BinaryOnOff] = Enumerated.of(Off, On)
}
