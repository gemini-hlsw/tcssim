// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.Eq
import lucuma.core.util.Enumerated

trait Beam extends Product with Serializable

object Beam {
  case object A extends Beam
  case object B extends Beam

  implicit val beamEq: Eq[Beam] = Eq.fromUniversalEquals

  implicit val beamEnumerated: Enumerated[Beam] = Enumerated.of(A, B)
}
