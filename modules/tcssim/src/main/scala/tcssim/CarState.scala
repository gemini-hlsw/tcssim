// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Eq
import lucuma.core.util.Enumerated

sealed trait CarState extends Product with Serializable

object CarState {

  case object IDLE   extends CarState
  case object PAUSED extends CarState
  case object BUSY   extends CarState
  case object ERROR  extends CarState

  implicit val cadStateEq: Eq[CarState] = Eq.fromUniversalEquals

  implicit val cadStateEnum: Enumerated[CarState] = Enumerated.of(IDLE, PAUSED, BUSY, ERROR)

}
