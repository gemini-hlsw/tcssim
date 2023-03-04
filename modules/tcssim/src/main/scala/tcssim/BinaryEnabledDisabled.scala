// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Eq
import lucuma.core.util.Enumerated

trait BinaryEnabledDisabled extends Product with Serializable

object BinaryEnabledDisabled {
  case object Disabled extends BinaryEnabledDisabled
  case object Enabled  extends BinaryEnabledDisabled

  implicit val bedEq: Eq[BinaryEnabledDisabled] = Eq.fromUniversalEquals

  implicit val bedEnumerated: Enumerated[BinaryEnabledDisabled] = Enumerated.of(Disabled, Enabled)
}
