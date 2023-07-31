// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import lucuma.core.util.Enumerated

enum CadDirective(val tag: String) derives Enumerated {
  case MARK   extends CadDirective("MARK")
  case CLEAR  extends CadDirective("CLEAR")
  case PRESET extends CadDirective("PRESET")
  case START  extends CadDirective("START")
  case STOP   extends CadDirective("STOP")
}
