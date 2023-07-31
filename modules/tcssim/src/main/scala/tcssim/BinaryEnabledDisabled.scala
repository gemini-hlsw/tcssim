// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import lucuma.core.util.Enumerated

enum BinaryEnabledDisabled(val tag: String) derives Enumerated {
  case Disabled extends BinaryEnabledDisabled("Disabled")
  case Enabled  extends BinaryEnabledDisabled("Enabled")
}
