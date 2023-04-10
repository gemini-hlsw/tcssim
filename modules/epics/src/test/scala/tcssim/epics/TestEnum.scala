// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import cats.Eq
import lucuma.core.util.Enumerated

sealed trait TestEnum extends Product with Serializable

object TestEnum {

  case object VAL0 extends TestEnum
  case object VAL1 extends TestEnum
  case object VAL2 extends TestEnum

  implicit val testEq: Eq[TestEnum] = Eq.fromUniversalEquals

  implicit val testEnumerated: Enumerated[TestEnum] =
    Enumerated.of[TestEnum](VAL0, VAL1, VAL2)

}
