// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import lucuma.core.util.Enumerated

enum BinaryYesNo(val tag: String) derives Enumerated {
  case No  extends BinaryYesNo("No")
  case Yes extends BinaryYesNo("Yes")
}
