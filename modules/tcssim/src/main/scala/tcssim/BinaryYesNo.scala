// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Eq
import lucuma.core.util.Enumerated

trait BinaryYesNo extends Product with Serializable

object BinaryYesNo {
  case object No  extends BinaryYesNo
  case object Yes extends BinaryYesNo

  implicit val binaryYesNoEq: Eq[BinaryYesNo] = Eq.fromUniversalEquals

  implicit val binaryYesNoEnum: Enumerated[BinaryYesNo] = Enumerated.of(No, Yes)
}
