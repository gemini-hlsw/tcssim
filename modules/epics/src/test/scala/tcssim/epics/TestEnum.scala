// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim.epics

import lucuma.core.util.Enumerated

enum TestEnum(val tag: String) derives Enumerated {
  case VAL0 extends TestEnum("VAL0")
  case VAL1 extends TestEnum("VAL1")
  case VAL2 extends TestEnum("VAL2")
}
