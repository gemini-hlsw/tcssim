// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import lucuma.core.util.Enumerated

enum CarState(val tag: String) derives Enumerated {
  case IDLE   extends CarState("IDLE")
  case PAUSED extends CarState("PAUSED")
  case BUSY   extends CarState("BUSY")
  case ERROR  extends CarState("ERROR")
}
